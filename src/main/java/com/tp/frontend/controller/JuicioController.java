package com.tp.frontend.controller;

import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.Asalto.AsaltoResponse; // Agregado
import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import com.tp.frontend.dto.Juicio.JuicioResponse;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.JuezService;
import com.tp.frontend.service.JuicioService;
import com.tp.frontend.service.PersonaDetenidaService;
import com.tp.frontend.service.AsaltoService; // Agregado
import com.tp.frontend.support.ErrorBinder;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/juicios")
public class JuicioController {

    private static final Logger log = LoggerFactory.getLogger(JuicioController.class);

    private final JuicioService juicioService;
    private final JuezService juezService;
    private final PersonaDetenidaService personaDetenidaService;
    private final AsaltoService asaltoService; // Agregado
    private final ErrorBinder errorBinder;

    public JuicioController(JuicioService juicioService,
                            JuezService juezService,
                            PersonaDetenidaService personaDetenidaService,
                            AsaltoService asaltoService, // Agregado
                            ErrorBinder errorBinder) {
        this.juicioService = juicioService;
        this.juezService = juezService;
        this.personaDetenidaService = personaDetenidaService;
        this.asaltoService = asaltoService; // Agregado
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
        List<AsaltoResponse> asaltos = asaltoService.list(token); // Agregado

        model.addAttribute("jueces", jueces);
        model.addAttribute("persona", personas);
        model.addAttribute("asaltos", asaltos); // Agregado

        model.addAttribute("hayJueces", !jueces.isEmpty());
        model.addAttribute("hayDetenidos", !personas.isEmpty());
        model.addAttribute("hayAsaltos", !asaltos.isEmpty()); // Agregado
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
            cargarCombos(model, token);
            return "juicios/CrearJuicio";
        }
        try {
            juicioService.create(token, form);
            return "redirect:/juicios";
        } catch (ApiErrorException ex) {
            errorBinder.bind(ex, br);
            cargarCombos(model, token);
            return "juicios/CrearJuicio";
        } catch (WebClientRequestException ex) {
            addGlobalError(br, "No pudimos conectarnos al servidor.");
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
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
        }
        try {
            juicioService.update(token, id, update);
            return "redirect:/juicios/" + id;
        } catch (ApiErrorException ex) {
            errorBinder.bind(ex, br);
            model.addAttribute("item", juicioService.get(token, id));
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
        } catch (WebClientRequestException ex) {
            addGlobalError(br, "Error de conexión.");
            model.addAttribute("item", juicioService.get(token, id));
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
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
            return "redirect:/juicios";
        } catch (ApiErrorException ex) {
            model.addAttribute("item", juicioService.get(token, id));
            return "juicios/ConfirmarBorrado";
        }
    }
}