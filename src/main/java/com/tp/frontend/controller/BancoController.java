package com.tp.frontend.controller;

import com.tp.frontend.dto.Banco.BancoRequest;
import com.tp.frontend.dto.Banco.BancoUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.BancoService;
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

@Controller
@RequestMapping("/bancos")
public class BancoController {

    private static final Logger log = LoggerFactory.getLogger(BancoController.class);

    private final BancoService service;
    private final ErrorBinder errorBinder;

    public BancoController(BancoService service, ErrorBinder errorBinder) {
        this.service = service;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        log.info("GET /bancos");
        model.addAttribute("items", service.list(jwt(session)));
        return "banco/ListaBancos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        log.info("GET /bancos/new");
        model.addAttribute("form", new BancoRequest());
        return "banco/CrearBanco";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") BancoRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        log.info("POST /bancos create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /bancos create SSR validation errors={}", br.getErrorCount());
            return "banco/CrearBanco";
        }

        try {
            service.create(jwt(session), form);
            log.info("POST /bancos create OK");
            return "redirect:/bancos";

        } catch (ApiErrorException ex) {
            log.warn("POST /bancos create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            return "banco/CrearBanco";

        } catch (WebClientRequestException ex) {
            log.warn("POST /bancos create transport error: {}", ex.getMessage());
            br.reject("backend.transport", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "banco/CrearBanco";

        } catch (Exception ex) {
            log.error("POST /bancos create unexpected", ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /bancos/{}", id);

        var item = service.get(jwt(session), id);
        var update = new BancoUpdate(item.getCodigo(), item.getDomicilioCentral());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        return "banco/DetalleBanco";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") BancoUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        log.info("POST /bancos/{} update={}", id, update);

        if (br.hasErrors()) {
            log.warn("POST /bancos/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("item", service.get(jwt(session), id));
            return "banco/DetalleBanco";
        }

        try {
            service.update(jwt(session), id, update);
            log.info("POST /bancos/{} update OK", id);
            return "redirect:/bancos/" + id;

        } catch (ApiErrorException ex) {
            log.warn("POST /bancos/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", service.get(jwt(session), id));
            return "banco/DetalleBanco";

        } catch (WebClientRequestException ex) {
            log.warn("POST /bancos/{} update transport error: {}", id, ex.getMessage());
            br.reject("backend.transport", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", service.get(jwt(session), id));
            return "banco/DetalleBanco";

        } catch (Exception ex) {
            log.error("POST /bancos/{} update unexpected", id, ex);
            throw ex;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /bancos/{}/confirm-delete", id);
        model.addAttribute("item", service.get(jwt(session), id));
        return "banco/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("POST /bancos/{}/delete", id);

        try {
            service.delete(jwt(session), id);
            log.info("POST /bancos/{}/delete OK", id);
            return "redirect:/bancos";

        } catch (ApiErrorException ex) {
            log.warn("POST /bancos/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", service.get(jwt(session), id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el banco. Intentá nuevamente.");
            return "banco/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /bancos/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("item", service.get(jwt(session), id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "banco/ConfirmarBorrado";
        }
    }
}