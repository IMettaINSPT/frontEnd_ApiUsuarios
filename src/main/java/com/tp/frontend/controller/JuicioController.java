package com.tp.frontend.controller;

import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.JuezService;
import com.tp.frontend.service.JuicioService;
import com.tp.frontend.service.PersonaDetenidaService;
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
    private final ErrorBinder errorBinder;

    public JuicioController(JuicioService juicioService,
                            JuezService juezService,
                             PersonaDetenidaService personaDetenidaService,
                            ErrorBinder errorBinder) {
        this.juicioService = juicioService;
        this.juezService = juezService;
        this.personaDetenidaService = personaDetenidaService;
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
        List<PersonaDetenidaResponse> delincuentes = personaDetenidaService.list(token);

        model.addAttribute("jueces", jueces);
        model.addAttribute("delincuentes", delincuentes);

        model.addAttribute("hayJueces", !jueces.isEmpty());
        model.addAttribute("hayDetenidos", !delincuentes.isEmpty());

        model.addAttribute("juecesMap",
                jueces.stream().collect(Collectors.toMap(JuezResponse::getId, Function.identity())));

        model.addAttribute("DelincuentesLabelMap",
                delincuentes.stream().collect(Collectors.toMap(PersonaDetenidaResponse::id, this::delincuenteLabel)));
    }

    /**
     * Genera un label "humano" sin mostrar id.
     * Usa reflexión para no romper compilación si el DTO cambia nombres de getters.
     * Si querés algo perfecto, reemplazá esto por campos concretos (ej: codigo/fecha/estado/etc.).
     */
    private String delincuenteLabel(PersonaDetenidaResponse c) {
        if (c == null) return "(Delincuente no disponible)";

        // Intentos comunes (ajustá si querés)
        String codigo = firstNonBlank(
                invokeString(c, "Codigo")
        );

        String nombre = invokeString(c, "nombre");

        String banda  =  invokeString(c.banda(), "numeroBanda");

        // Armamos un label compacto sin IDs
        String base = (codigo != null) ? codigo : "Delincuente";
        if (nombre != null && !nombre.isBlank()) base = base + " • " + nombre;
        if (banda != null && !banda.isBlank()) base = base + " • " + banda;

        // Último recurso: toString (ojo: si tu toString incluye id, cambiá esto)
        if ("Delincuente".equals(base)) {
            String ts = String.valueOf(c);
            if (ts != null && !ts.isBlank() && !ts.matches(".*\\b\\d+\\b.*")) { // evita strings que parezcan puro id
                return ts;
            }
        }

        return base;
    }

    private String invokeString(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object v = m.invoke(target);
            return v == null ? null : String.valueOf(v);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    // =========================
    // LISTA
    // =========================

    @GetMapping
    public String list(HttpSession session, Model model) {
        log.info("GET /juicios");
        model.addAttribute("items", juicioService.list(jwt(session)));
        return "juicios/ListaJuicios";
    }

    // =========================
    // CREATE
    // =========================

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
        log.info("POST /juicios create form={}", form);

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
            log.warn("POST /juicios create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            cargarCombos(model, token);
            return "juicios/CrearJuicio";
        }
    }

    // =========================
    // DETALLE / UPDATE
    // =========================

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /juicios/{}", id);

        var item = juicioService.get(token, id);

        var update = new JuicioUpdate(item.getFecha(), item.getResultado(),item.getJuezId(), item.getPersonaDetenidaId());

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
        log.info("POST /juicios/{} update={}", id, update);

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
            log.warn("POST /juicios/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", juicioService.get(token, id));
            cargarCombos(model, token);
            return "juicios/DetalleJuicio";
        }
    }

    // =========================
    // DELETE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /juicios/{}/confirm-delete", id);

        model.addAttribute("item", juicioService.get(token, id));
        cargarCombos(model, token);

        return "juicios/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /juicios/{}/delete", id);

        try {
            juicioService.delete(token, id);
            return "redirect:/juicios";

        } catch (ApiErrorException ex) {
            model.addAttribute("item", juicioService.get(token, id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el juicio. Intentá nuevamente.");

            cargarCombos(model, token);
            return "juicios/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            model.addAttribute("item", juicioService.get(token, id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");

            cargarCombos(model, token);
            return "juicios/ConfirmarBorrado";
        }
    }
}