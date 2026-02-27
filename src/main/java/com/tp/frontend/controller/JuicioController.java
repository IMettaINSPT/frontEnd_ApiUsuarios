package com.tp.frontend.controller;

import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.Asalto.AsaltoResponse;
import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import com.tp.frontend.dto.Juicio.JuicioResponse;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.JuezService;
import com.tp.frontend.service.JuicioService;
import com.tp.frontend.service.PersonaDetenidaService;
import com.tp.frontend.service.AsaltoService;
import com.tp.frontend.support.ErrorBinder;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.List;

@Controller
@RequestMapping("/juicios")
public class JuicioController {

    private static final Logger log = LoggerFactory.getLogger(JuicioController.class);

    private final JuicioService juicioService;
    private final JuezService juezService;
    private final PersonaDetenidaService personaDetenidaService;
    private final AsaltoService asaltoService;
    private final ErrorBinder errorBinder;

    @Value("${frontend.backend-base-url}")
    private String backendApiUrl;

    public JuicioController(JuicioService juicioService,
                            JuezService juezService,
                            PersonaDetenidaService personaDetenidaService,
                            AsaltoService asaltoService,
                            ErrorBinder errorBinder) {
        this.juicioService = juicioService;
        this.juezService = juezService;
        this.personaDetenidaService = personaDetenidaService;
        this.asaltoService = asaltoService;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    private void cargarCombos(Model model, String token) {
        List<JuezResponse> jueces = juezService.list(token);
        List<PersonaDetenidaResponse> personas = personaDetenidaService.list(token);
        List<AsaltoResponse> asaltos = asaltoService.list(token);

        model.addAttribute("jueces", jueces);
        model.addAttribute("persona", personas);
        model.addAttribute("asaltos", asaltos);

        model.addAttribute("hayJueces", !jueces.isEmpty());
        model.addAttribute("hayDetenidos", !personas.isEmpty());
        model.addAttribute("hayAsaltos", !asaltos.isEmpty());
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        log.info("GET /juicios");
        model.addAttribute("items", juicioService.list(jwt(session)));
        return "juicios/ListaJuicios";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /juicios/new");
        model.addAttribute("form", new JuicioRequest());
        model.addAttribute("backendUrl", backendApiUrl);
        cargarCombos(model, token);
        return "juicios/CrearJuicio";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") JuicioRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {
        String token = jwt(session);
        if (br.hasErrors()) {
            model.addAttribute("backendUrl", backendApiUrl);
            cargarCombos(model, token);
            return "juicios/CrearJuicio";
        }
        try {
            juicioService.create(token, form);
            return "redirect:/juicios?msg=Juicio registrado exitosamente";
        } catch (ApiErrorException ex) {
            handleBusinessError(ex, br);
            model.addAttribute("backendUrl", backendApiUrl);
            cargarCombos(model, token);
            return "juicios/CrearJuicio";
        } catch (WebClientRequestException ex) {
            addGlobalError(br, "No pudimos conectarnos al servidor.");
            model.addAttribute("backendUrl", backendApiUrl);
            cargarCombos(model, token);
            return "juicios/CrearJuicio";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /juicios/{}", id);

        JuicioResponse item = juicioService.get(token, id);

        JuicioUpdate update = new JuicioUpdate();
        update.setExpediente(item.getExpediente());
        update.setFechaJuicio(item.getFechaJuicio());
        update.setCondenado(item.getCondenado());

        if (item.getJuez() != null) update.setJuezId(item.getJuez().getId());
        if (item.getAsalto() != null) update.setAsaltoId(item.getAsalto().getId());
        if (item.getPersona() != null) update.setPersonaDetenidaId(item.getPersona().id());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        model.addAttribute("backendUrl", backendApiUrl);
        cargarCombos(model, token);

        return "juicios/DetalleJuicio";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") JuicioUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {
        String token = jwt(session);
        if (br.hasErrors()) {
            model.addAttribute("item", juicioService.get(token, id));
            model.addAttribute("backendUrl", backendApiUrl);
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
        }
        try {
            juicioService.update(token, id, update);
            return "redirect:/juicios?msg=Juicio actualizado correctamente";
        } catch (ApiErrorException ex) {
            handleBusinessError(ex, br);
            model.addAttribute("item", juicioService.get(token, id));
            model.addAttribute("backendUrl", backendApiUrl);
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
        } catch (WebClientRequestException ex) {
            addGlobalError(br, "Error de conexión.");
            model.addAttribute("item", juicioService.get(token, id));
            model.addAttribute("backendUrl", backendApiUrl);
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
        }
    }

    /**
     * Método privado para diferenciar el texto según el mensaje del backend
     */
    private void handleBusinessError(ApiErrorException ex, BindingResult br) {
        // Usamos solo getMessage() que es lo que existe en ApiErrorException
        String msg = (ex.getMessage() != null) ? ex.getMessage().toUpperCase() : "";

        // En tus logs el error viene con "[EXPEDIENTE_DUPLICADO]" o contiene "EXPEDIENTE"
        if (msg.contains("EXPEDIENTE")) {
            addGlobalError(br, "Ese nro de expediente ya existe.");
        }
        // Si no es expediente, pero el mensaje indica algo de fechas
        else if (msg.contains("FECHA") || msg.contains("ANTERIOR")) {
            addGlobalError(br, "No se pudo procesar la solicitud. Revisá las fechas ingresadas e intentá de nuevo.");
        }
        // Mensaje por defecto basado en tu requerimiento actual
        else {
            addGlobalError(br, "No se pudo procesar la solicitud. Revisá las fechas ingresadas e intentá de nuevo.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        model.addAttribute("item", juicioService.get(token, id));
        return "juicios/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        try {
            juicioService.delete(token, id);
            return "redirect:/juicios?msg=Juicio eliminado correctamente";
        } catch (ApiErrorException ex) {
            model.addAttribute("item", juicioService.get(token, id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el juicio.");
            return "juicios/ConfirmarBorrado";
        }
    }
}