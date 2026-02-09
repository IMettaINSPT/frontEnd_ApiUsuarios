package com.tp.frontend.controller;

import com.tp.frontend.dto.Banda.BandaResponse;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaRequest;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.BandaService;
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

import java.util.List;

@Controller
@RequestMapping("/personas-detenidas")
public class PersonasDetenidasController {

    private static final Logger log = LoggerFactory.getLogger(PersonasDetenidasController.class);

    private final PersonaDetenidaService service;
    private final BandaService bandaService;
    private final ErrorBinder errorBinder;

    public PersonasDetenidasController(PersonaDetenidaService service, BandaService bandaService, ErrorBinder errorBinder) {
        this.service = service;
        this.bandaService = bandaService;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    private void cargarBandas(Model model, String token) {
        List<BandaResponse> bandas = bandaService.list(token);
        model.addAttribute("bandas", bandas);
        model.addAttribute("hayBandas", !bandas.isEmpty());
    }

    // =========================
    // LISTA / CREAR
    // =========================

    @GetMapping
    public String list(HttpSession session, Model model) {
        log.info("GET /personas-detenidas");
        model.addAttribute("items", service.list(jwt(session)));
        return "personasDetenidas/ListaPersonasDetenidas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /personas-detenidas/new");

        model.addAttribute("form", new PersonaDetenidaRequest());
        cargarBandas(model, token);

        return "personasDetenidas/CrearPersonaDetenida";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") PersonaDetenidaRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /personas-detenidas create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /personas-detenidas create SSR validation errors={}", br.getErrorCount());
            cargarBandas(model, token);
            return "personasDetenidas/CrearPersonaDetenida";
        }

        try {
            service.create(token, form);
            log.info("POST /personas-detenidas create OK");
            return "redirect:/personas-detenidas";

        } catch (ApiErrorException ex) {
            log.warn("POST /personas-detenidas create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            cargarBandas(model, token);
            return "personasDetenidas/CrearPersonaDetenida";

        } catch (WebClientRequestException ex) {
            log.warn("POST /personas-detenidas create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            cargarBandas(model, token);
            return "personasDetenidas/CrearPersonaDetenida";
        }
    }

    // =========================
    // DETALLE / UPDATE
    // =========================

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /personas-detenidas/{}", id);

        var item = service.get(token, id);

        var update = new PersonaDetenidaUpdate(
                item.nombre(),
                item.codigo(),
                item.banda() != null ? item.banda().id() : null
        );
        model.addAttribute("item", item);
        model.addAttribute("update", update);

        cargarBandas(model, token);
        return "personasDetenidas/DetallePersonaDetenida";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") PersonaDetenidaUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /personas-detenidas/{} update={}", id, update);

        if (br.hasErrors()) {
            log.warn("POST /personas-detenidas/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("item", service.get(token, id));
            cargarBandas(model, token);
            return "personasDetenidas/DetallePersonaDetenida";
        }

        try {
            service.update(token, id, update);
            log.info("POST /personas-detenidas/{} update OK", id);
            return "redirect:/personas-detenidas/" + id;

        } catch (ApiErrorException ex) {
            log.warn("POST /personas-detenidas/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", service.get(token, id));
            cargarBandas(model, token);
            return "personasDetenidas/DetallePersonaDetenida";

        } catch (WebClientRequestException ex) {
            log.warn("POST /personas-detenidas/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", service.get(token, id));
            cargarBandas(model, token);
            return "personasDetenidas/DetallePersonaDetenida";
        }
    }

    // =========================
    // DELETE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /personas-detenidas/{}/confirm-delete", id);
        model.addAttribute("item", service.get(jwt(session), id));
        return "personasDetenidas/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /personas-detenidas/{}/delete", id);

        try {
            service.delete(token, id);
            log.info("POST /personas-detenidas/{}/delete OK", id);
            return "redirect:/personas-detenidas";

        } catch (ApiErrorException ex) {
            log.warn("POST /personas-detenidas/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", service.get(token, id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar la persona detenida. Intentá nuevamente.");
            return "personasDetenidas/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /personas-detenidas/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("item", service.get(token, id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "personasDetenidas/ConfirmarBorrado";
        }
    }
}