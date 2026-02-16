package com.tp.frontend.controller;

import com.tp.frontend.dto.contrato.*;
import com.tp.frontend.dto.Sucursal.SucursalResponse;
import com.tp.frontend.dto.Vigilante.VigilanteResponse;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.ContratoService;
import com.tp.frontend.service.SucursalService;
import com.tp.frontend.service.VigilanteService;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contratos")
public class ContratosController {

    private static final Logger log = LoggerFactory.getLogger(ContratosController.class);

    private final ContratoService contratoService;
    private final SucursalService sucursalService;
    private final VigilanteService vigilanteService;
    private final ErrorBinder errorBinder;

    public ContratosController(ContratoService contratoService,
                              SucursalService sucursalService,
                              VigilanteService vigilanteService,
                              ErrorBinder errorBinder) {
        this.contratoService = contratoService;
        this.sucursalService = sucursalService;
        this.vigilanteService = vigilanteService;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    /**
     * Carga combos + maps/labels para mostrar "humano" (sin IDs).
     *
     * Model:
     * - sucursales, vigilantes (para selects)
     * - sucursalesMap (id -> SucursalResponse)
     * - vigilantesMap (id -> VigilanteResponse)
     * - sucursalLabelMap (id -> String)
     * - vigilanteLabelMap (id -> String)
     */
    private void cargarCombos(Model model, String token) {
        List<SucursalResponse> sucursales = sucursalService.list(token);
        List<VigilanteResponse> vigilantes = vigilanteService.list(token);

        model.addAttribute("sucursales", sucursales);
        model.addAttribute("vigilantes", vigilantes);

        model.addAttribute("haySucursales", !sucursales.isEmpty());
        model.addAttribute("hayVigilantes", !vigilantes.isEmpty());

        Map<Long, SucursalResponse> sucursalesMap =
                sucursales.stream().collect(Collectors.toMap(SucursalResponse::getId, Function.identity()));
        Map<Long, VigilanteResponse> vigilantesMap =
                vigilantes.stream().collect(Collectors.toMap(VigilanteResponse::getId, Function.identity()));

        model.addAttribute("sucursalesMap", sucursalesMap);
        model.addAttribute("vigilantesMap", vigilantesMap);

        model.addAttribute("sucursalLabelMap",
                sucursales.stream().collect(Collectors.toMap(SucursalResponse::getId, this::sucursalLabel)));

        model.addAttribute("vigilanteLabelMap",
                vigilantes.stream().collect(Collectors.toMap(VigilanteResponse::getId, this::vigilanteLabel)));
    }

    private String sucursalLabel(SucursalResponse s) {
        if (s == null) return "(sucursal no disponible)";
        // Ajustá según tu DTO real:
        // típico: codigo + domicilio + (bancoCodigo)
        String codigo = s.getCodigo() != null ? s.getCodigo() : "Sucursal";
        String dom = s.getDomicilio() != null ? s.getDomicilio() : "";
        String banco = null;
        try { banco = s.getBancoCodigo(); } catch (Exception ignored) {}

        String label = codigo;
        if (!dom.isBlank()) label += " • " + dom;
        if (banco != null && !banco.isBlank()) label += " • " + banco;
        return label;
    }

    private String vigilanteLabel(VigilanteResponse v) {
        if (v == null) return "(vigilante no disponible)";
        // Ajustá según tu DTO real:
        String codigo = v.getCodigo() != null ? v.getCodigo() : "Vigilante";
        String edad = null;
        try { edad = String.valueOf(v.getEdad()); } catch (Exception ignored) {}
        return (edad != null && !edad.equals("0")) ? (codigo + " • " + edad + " años") : codigo;
    }

    /** Label humano del contrato (para lista y confirm delete) */
    private String contratoLabel(ContratoResponse c, Map<Long, String> sucursalLabelMap, Map<Long, String> vigilanteLabelMap) {
        if (c == null) return "(contrato no disponible)";

        String s = sucursalLabelMap.get(c.getSucursalId());
        String v = vigilanteLabelMap.get(c.getVigilanteId());

        String rango = "";
        if (c.getFechaContrato() != null) rango += c.getFechaContrato();
        if (rango.isBlank()) rango = "Sin fechas";

        String label = (s != null ? s : "Sucursal N/D") + " • " + (v != null ? v : "Vigilante N/D");
        label += " • " + rango + " • " ;

        return label;
    }

    // =========================
    // LISTA
    // =========================

    @GetMapping
    public String list(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /contratos");

        // Para mostrar etiquetas humanas en lista (si tu ListaContratos lo usa)
        cargarCombos(model, token);

        model.addAttribute("items", contratoService.list(token));
        return "contratos/ListaContratos";
    }

    // =========================
    // CREATE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /contratos/new");

        model.addAttribute("form", new ContratoRequest());
        cargarCombos(model, token);

        return "contratos/CrearContrato";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") ContratoRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {
        String token = jwt(session);
        log.info("POST /contratos create form={}", form);

        if (br.hasErrors()) {
            cargarCombos(model, token);
            return "contratos/CrearContrato";
        }

        try {
            contratoService.create(token, form);
            return "redirect:/contratos";

        } catch (ApiErrorException ex) {
            errorBinder.bind(ex, br);
            cargarCombos(model, token);
            return "contratos/CrearContrato";

        } catch (WebClientRequestException ex) {
            log.warn("POST /contratos create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            cargarCombos(model, token);
            return "contratos/CrearContrato";
        }
    }

    // =========================
    // DETALLE / UPDATE
    // =========================

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /contratos/{}", id);

        var item = contratoService.get(token, id);

        var update = new ContratoUpdate(
                item.getFechaContrato(),
                item.getConArma(),
                item.getSucursalId(),
                item.getVigilanteId(),
                item.getFechaFin()
        );

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        cargarCombos(model, token);

        // label humano del contrato para header (sin id)
        @SuppressWarnings("unchecked")
        Map<Long, String> sucursalLabelMap = (Map<Long, String>) model.getAttribute("sucursalLabelMap");
        @SuppressWarnings("unchecked")
        Map<Long, String> vigilanteLabelMap = (Map<Long, String>) model.getAttribute("vigilanteLabelMap");
        model.addAttribute("contratoLabel", contratoLabel(item, sucursalLabelMap, vigilanteLabelMap));

        return "contratos/DetalleContrato";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") ContratoUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {
        String token = jwt(session);
        log.info("POST /contratos/{} update={}", id, update);

        if (br.hasErrors()) {
            model.addAttribute("item", contratoService.get(token, id));
            cargarCombos(model, token);
            return "contratos/DetalleContrato";
        }

        try {
            contratoService.update(token, id, update);
            return "redirect:/contratos/" + id;

        } catch (ApiErrorException ex) {
            errorBinder.bind(ex, br);
            model.addAttribute("item", contratoService.get(token, id));
            cargarCombos(model, token);
            return "contratos/DetalleContrato";

        } catch (WebClientRequestException ex) {
            log.warn("POST /contratos/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", contratoService.get(token, id));
            cargarCombos(model, token);
            return "contratos/DetalleContrato";
        }
    }

    // =========================
    // DELETE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /contratos/{}/confirm-delete", id);

        var item = contratoService.get(token, id);
        model.addAttribute("item", item);

        cargarCombos(model, token);

        @SuppressWarnings("unchecked")
        Map<Long, String> sucursalLabelMap = (Map<Long, String>) model.getAttribute("sucursalLabelMap");
        @SuppressWarnings("unchecked")
        Map<Long, String> vigilanteLabelMap = (Map<Long, String>) model.getAttribute("vigilanteLabelMap");
        model.addAttribute("contratoLabel", contratoLabel(item, sucursalLabelMap, vigilanteLabelMap));

        return "contratos/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /contratos/{}/delete", id);

        try {
            contratoService.delete(token, id);
            return "redirect:/contratos";

        } catch (ApiErrorException ex) {
            var item = contratoService.get(token, id);
            model.addAttribute("item", item);
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el contrato. Intentá nuevamente.");

            cargarCombos(model, token);

            @SuppressWarnings("unchecked")
            Map<Long, String> sucursalLabelMap = (Map<Long, String>) model.getAttribute("sucursalLabelMap");
            @SuppressWarnings("unchecked")
            Map<Long, String> vigilanteLabelMap = (Map<Long, String>) model.getAttribute("vigilanteLabelMap");
            model.addAttribute("contratoLabel", contratoLabel(item, sucursalLabelMap, vigilanteLabelMap));

            return "contratos/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            var item = contratoService.get(token, id);
            model.addAttribute("item", item);
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");

            cargarCombos(model, token);

            @SuppressWarnings("unchecked")
            Map<Long, String> sucursalLabelMap = (Map<Long, String>) model.getAttribute("sucursalLabelMap");
            @SuppressWarnings("unchecked")
            Map<Long, String> vigilanteLabelMap = (Map<Long, String>) model.getAttribute("vigilanteLabelMap");
            model.addAttribute("contratoLabel", contratoLabel(item, sucursalLabelMap, vigilanteLabelMap));

            return "contratos/ConfirmarBorrado";
        }
    }
}