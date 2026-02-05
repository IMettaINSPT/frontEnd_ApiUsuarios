package com.tp.frontend.controller;

import com.tp.frontend.dto.Banda.BandaRequest;
import com.tp.frontend.dto.Banda.BandaUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.BandaService;
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
@RequestMapping("/bandas")
public class BandasController {

    private static final Logger log = LoggerFactory.getLogger(BandasController.class);

    private final BandaService service;
    private final ErrorBinder errorBinder;

    public BandasController(BandaService service, ErrorBinder errorBinder) {
        this.service = service;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        log.info("GET /bandas");
        model.addAttribute("items", service.list(jwt(session)));
        return "bandas/ListaBandas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        log.info("GET /bandas/new");
        model.addAttribute("form", new BandaRequest());
        return "bandas/CrearBanda";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") BandaRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        log.info("POST /bandas create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /bandas create SSR validation errors={}", br.getErrorCount());
            return "bandas/CrearBanda";
        }

        try {
            service.create(jwt(session), form);
            log.info("POST /bandas create OK");
            return "redirect:/bandas";

        } catch (ApiErrorException ex) {
            log.warn("POST /bandas create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            return "bandas/CrearBanda";

        } catch (WebClientRequestException ex) {
            log.warn("POST /bandas create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "bandas/CrearBanda";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /bandas/{}", id);

        var item = service.get(token, id);


        var update = new BandaUpdate(item.numeroBanda(),item.numeroMiembros());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        return "bandas/DetalleBanda";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") BandaUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /bandas/{} update={}", id, update);

        if (br.hasErrors()) {
            log.warn("POST /bandas/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("item", service.get(token, id));
            return "bandas/DetalleBanda";
        }

        try {
            service.update(token, id, update);
            log.info("POST /bandas/{} update OK", id);
            return "redirect:/bandas/" + id;

        } catch (ApiErrorException ex) {
            log.warn("POST /bandas/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", service.get(token, id));
            return "bandas/DetalleBanda";

        } catch (WebClientRequestException ex) {
            log.warn("POST /bandas/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", service.get(token, id));
            return "bandas/DetalleBanda";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /bandas/{}/confirm-delete", id);
        model.addAttribute("item", service.get(jwt(session), id));
        return "bandas/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /bandas/{}/delete", id);

        try {
            service.delete(token, id);
            log.info("POST /bandas/{}/delete OK", id);
            return "redirect:/bandas";

        } catch (ApiErrorException ex) {
            log.warn("POST /bandas/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", service.get(token, id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar la banda. Intentá nuevamente.");
            return "bandas/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /bandas/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("item", service.get(token, id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "bandas/ConfirmarBorrado";
        }
    }
}