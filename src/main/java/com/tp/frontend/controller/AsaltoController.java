package com.tp.frontend.controller;

import com.tp.frontend.dto.Asalto.AsaltoRequest;
import com.tp.frontend.dto.Asalto.AsaltoUpdate;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Sucursal.SucursalResponse;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.AsaltoService;
import com.tp.frontend.service.PersonaDetenidaService;
import com.tp.frontend.service.SucursalService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asaltos")
public class AsaltoController {

    private static final Logger log = LoggerFactory.getLogger(AsaltoController.class);

    private final AsaltoService asaltoService;
    private final SucursalService sucursalService;
    private final PersonaDetenidaService personaDetenidaService;
    private final ErrorBinder errorBinder;

    public AsaltoController(AsaltoService asaltoService,
                            SucursalService sucursalService,
                            PersonaDetenidaService personaDetenidaService,
                            ErrorBinder errorBinder) {
        this.asaltoService = asaltoService;
        this.sucursalService = sucursalService;
        this.personaDetenidaService = personaDetenidaService;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    // =========================
    // Combos + Maps (labels humanos)
    // =========================

    private List<SucursalResponse> sucursales(String token) {
        return sucursalService.list(token);
    }

    private List<PersonaDetenidaResponse> personas(String token) {
        return personaDetenidaService.list(token);
    }

    private Map<Long, SucursalResponse> sucursalesMap(String token) {
        return sucursales(token).stream()
                .collect(Collectors.toMap(SucursalResponse::getId, Function.identity()));
    }

    private Map<Long, PersonaDetenidaResponse> personasMap(String token) {
        return personas(token).stream()
                .collect(Collectors.toMap(PersonaDetenidaResponse::id, Function.identity()));
    }

    /** Listas para selects + maps para renders sin IDs */
    private void cargarCombos(Model model, String token) {
        List<SucursalResponse> sucs = sucursales(token);
        List<PersonaDetenidaResponse> pers = personas(token);

        model.addAttribute("sucursales", sucs);
        model.addAttribute("personas", pers);

        model.addAttribute("haySucursales", !sucs.isEmpty());
        model.addAttribute("hayPersonas", !pers.isEmpty());

        model.addAttribute("sucursalesMap", sucs.stream()
                .collect(Collectors.toMap(SucursalResponse::getId, Function.identity())));

        model.addAttribute("personasMap", pers.stream()
                .collect(Collectors.toMap(PersonaDetenidaResponse::id, Function.identity())));
    }

    // =========================
    // LISTA
    // =========================

    @GetMapping
    public String list(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /asaltos");

        model.addAttribute("items", asaltoService.list(token));
        model.addAttribute("sucursalesMap", sucursalesMap(token));
        model.addAttribute("personasMap", personasMap(token));

        return "asaltos/ListaAsaltos";
    }

    // =========================
    // REPORTE (GET /asaltos/reporte?...)
    // =========================

    @GetMapping("/reporte")
    public String reporte(@RequestParam(required = false) Long sucursalId,
                          @RequestParam(required = false) LocalDate fecha,
                          @RequestParam(required = false) LocalDate desde,
                          @RequestParam(required = false) LocalDate hasta,
                          HttpSession session,
                          Model model) {

        String token = jwt(session);
        log.info("GET /asaltos/reporte sucursalId={} fecha={} desde={} hasta={}",
                sucursalId, fecha, desde, hasta);

        // datos para filtros
        model.addAttribute("sucursales", sucursales(token));
        model.addAttribute("sucursalesMap", sucursalesMap(token));
        model.addAttribute("personasMap", personasMap(token));

        // para mantener valores en inputs
        model.addAttribute("filterSucursalId", sucursalId);
        model.addAttribute("filterFecha", fecha);
        model.addAttribute("filterDesde", desde);
        model.addAttribute("filterHasta", hasta);

        model.addAttribute("items", asaltoService.reporte(token, sucursalId, fecha, desde, hasta));

        return "asaltos/ReporteAsaltos";
    }

    // =========================
    // CREATE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /asaltos/new");

        model.addAttribute("form", new AsaltoRequest());
        cargarCombos(model, token);

        return "asaltos/CrearAsalto";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") AsaltoRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /asaltos create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /asaltos create SSR validation errors={}", br.getErrorCount());
            cargarCombos(model, token);
            return "asaltos/CrearAsalto";
        }

        try {
            asaltoService.create(token, form);
            log.info("POST /asaltos create OK");
            return "redirect:/asaltos";

        } catch (ApiErrorException ex) {
            log.warn("POST /asaltos create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            cargarCombos(model, token);
            return "asaltos/CrearAsalto";

        } catch (WebClientRequestException ex) {
            log.warn("POST /asaltos create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            cargarCombos(model, token);
            return "asaltos/CrearAsalto";
        }
    }

    // =========================
    // DETALLE / UPDATE
    // =========================

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /asaltos/{}", id);

        var item = asaltoService.get(token, id);

        // Ajustá si tu DTO real cambia nombres:
        var update = new AsaltoUpdate(item.getFechaAsalto(), item.getSucursalId(), item.getPersonaDetenidaId());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        cargarCombos(model, token);
        return "asaltos/DetalleAsalto";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") AsaltoUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /asaltos/{} update={}", id, update);

        if (br.hasErrors()) {
            log.warn("POST /asaltos/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("item", asaltoService.get(token, id));
            cargarCombos(model, token);
            return "asaltos/DetalleAsalto";
        }

        try {
            asaltoService.update(token, id, update);
            log.info("POST /asaltos/{} update OK", id);
            return "redirect:/asaltos/" + id;

        } catch (ApiErrorException ex) {
            log.warn("POST /asaltos/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", asaltoService.get(token, id));
            cargarCombos(model, token);
            return "asaltos/DetalleAsalto";

        } catch (WebClientRequestException ex) {
            log.warn("POST /asaltos/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", asaltoService.get(token, id));
            cargarCombos(model, token);
            return "asaltos/DetalleAsalto";
        }
    }

    // =========================
    // DELETE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /asaltos/{}/confirm-delete", id);

        model.addAttribute("item", asaltoService.get(token, id));
        model.addAttribute("sucursalesMap", sucursalesMap(token));
        model.addAttribute("personasMap", personasMap(token));

        return "asaltos/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /asaltos/{}/delete", id);

        try {
            asaltoService.delete(token, id);
            log.info("POST /asaltos/{}/delete OK", id);
            return "redirect:/asaltos";

        } catch (ApiErrorException ex) {
            log.warn("POST /asaltos/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", asaltoService.get(token, id));
            model.addAttribute("sucursalesMap", sucursalesMap(token));
            model.addAttribute("personasMap", personasMap(token));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el asalto. Intentá nuevamente.");
            return "asaltos/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /asaltos/{}/delete transport error: {}", id, ex.getMessage());

            model.addAttribute("item", asaltoService.get(token, id));
            model.addAttribute("sucursalesMap", sucursalesMap(token));
            model.addAttribute("personasMap", personasMap(token));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "asaltos/ConfirmarBorrado";
        }
    }
}