package com.tp.frontend.controller;

import com.tp.frontend.dto.Banco.BancoResponse;
import com.tp.frontend.dto.Sucursal.SucursalRequest;
import com.tp.frontend.dto.Sucursal.SucursalUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.BancoService;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sucursales")
public class SucursalController {

    private static final Logger log = LoggerFactory.getLogger(SucursalController.class);

    private final SucursalService sucursalService;
    private final BancoService bancoService;
    private final ErrorBinder errorBinder;

    public SucursalController(SucursalService sucursalService, BancoService bancoService, ErrorBinder errorBinder) {
        this.sucursalService = sucursalService;
        this.bancoService = bancoService;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private List<BancoResponse> bancos(String token) {
        return bancoService.list(token);
    }

    private Map<Long, BancoResponse> bancosMap(String token) {
        return bancos(token).stream().collect(Collectors.toMap(BancoResponse::getId, Function.identity()));
    }

    private void cargarBancos(Model model, String token) {
        var disponibles = bancos(token);
        model.addAttribute("bancos", disponibles);
        model.addAttribute("hayBancos", !disponibles.isEmpty());
        model.addAttribute("bancosMap", bancosMap(token));
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /sucursales");

        model.addAttribute("items", sucursalService.list(token));
        model.addAttribute("bancosMap", bancosMap(token));

        return "sucursales/ListaSucursales";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /sucursales/new");

        model.addAttribute("form", new SucursalRequest());
        cargarBancos(model, token);

        return "sucursales/CrearSucursal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") SucursalRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /sucursales create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /sucursales create SSR validation errors={}", br.getErrorCount());
            cargarBancos(model, token);
            return "sucursales/CrearSucursal";
        }

        try {
            sucursalService.create(token, form);
            log.info("POST /sucursales create OK");
            return "redirect:/sucursales";

        } catch (ApiErrorException ex) {
            log.warn("POST /sucursales create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            cargarBancos(model, token);
            return "sucursales/CrearSucursal";

        } catch (WebClientRequestException ex) {
            log.warn("POST /sucursales create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            cargarBancos(model, token);
            return "sucursales/CrearSucursal";

        } catch (Exception ex) {
            log.error("POST /sucursales create unexpected", ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /sucursales/{}", id);

        var item = sucursalService.get(token, id);
        var update = new SucursalUpdate(item.getCodigo(), item.getDomicilio(), item.getNroEmpleados(), item.getBancoId());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        model.addAttribute("bancos", bancos(token));
        model.addAttribute("bancosMap", bancosMap(token));

        return "sucursales/DetalleSucursal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") SucursalUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /sucursales/{} update={}", id, update);

        if (br.hasErrors()) {
            log.warn("POST /sucursales/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("item", sucursalService.get(token, id));
            model.addAttribute("bancos", bancos(token));
            model.addAttribute("bancosMap", bancosMap(token));
            return "sucursales/DetalleSucursal";
        }

        try {
            sucursalService.update(token, id, update);
            log.info("POST /sucursales/{} update OK", id);
            return "redirect:/sucursales/" + id;

        } catch (ApiErrorException ex) {
            log.warn("POST /sucursales/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", sucursalService.get(token, id));
            model.addAttribute("bancos", bancos(token));
            model.addAttribute("bancosMap", bancosMap(token));
            return "sucursales/DetalleSucursal";

        } catch (WebClientRequestException ex) {
            log.warn("POST /sucursales/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");

            model.addAttribute("item", sucursalService.get(token, id));
            model.addAttribute("bancos", bancos(token));
            model.addAttribute("bancosMap", bancosMap(token));
            return "sucursales/DetalleSucursal";

        } catch (Exception ex) {
            log.error("POST /sucursales/{} update unexpected", id, ex);
            throw ex;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /sucursales/{}/confirm-delete", id);
        model.addAttribute("item", sucursalService.get(jwt(session), id));
        return "sucursales/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("POST /sucursales/{}/delete", id);
        String token = jwt(session);

        try {
            sucursalService.delete(token, id);
            log.info("POST /sucursales/{}/delete OK", id);
            return "redirect:/sucursales";

        } catch (ApiErrorException ex) {
            log.warn("POST /sucursales/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", sucursalService.get(token, id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar la sucursal. Intentá nuevamente.");
            return "sucursales/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /sucursales/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("item", sucursalService.get(token, id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "sucursales/ConfirmarBorrado";
        }
    }
}