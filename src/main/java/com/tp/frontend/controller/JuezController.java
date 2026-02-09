package com.tp.frontend.controller;

import com.tp.frontend.dto.Juez.JuezRequest;
import com.tp.frontend.dto.Juez.JuezUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.JuezService;
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
@RequestMapping("/jueces")
public class JuezController {

    private static final Logger log = LoggerFactory.getLogger(JuezController.class);

    private final JuezService service;
    private final ErrorBinder errorBinder;

    public JuezController(JuezService service, ErrorBinder errorBinder) {
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
        log.info("GET /jueces");
        model.addAttribute("items", service.list(jwt(session)));
        return "juez/ListaJueces";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        log.info("GET /juez/new");
        model.addAttribute("form", new JuezRequest());
        return "juez/CrearJuez";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") JuezRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        log.info("POST /jueces create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /jueces create SSR validation errors={}", br.getErrorCount());
            return "juez/CrearJuez";
        }

        try {
            service.create(jwt(session), form);
            log.info("POST /jueces create OK");
            return "redirect:/jueces";

        } catch (ApiErrorException ex) {
            log.warn("POST /jueces create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            return "juez/CrearJuez";

        } catch (WebClientRequestException ex) {
            log.warn("POST /jueces create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "juez/CrearJuez";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /juez/{}", id);

        var item = service.get(token, id);

        var update = new JuezUpdate(item.getCodigo(), item.getNombre(),item.getApellido());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        return "juez/DetalleJuez";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") JuezUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /juez/{} update={}", id, update);

        if (br.hasErrors()) {
            model.addAttribute("item", service.get(token, id));
            return "juez/DetalleJuez";
        }

        try {
            service.update(token, id, update);
            log.info("POST /juez/{} update OK", id);
            return "redirect:/jueces";

        } catch (ApiErrorException ex) {
            log.warn("POST /juez/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", service.get(token, id));
            return "juez/DetalleJuez";

        } catch (WebClientRequestException ex) {
            log.warn("POST /juez/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", service.get(token, id));
            return "juez/DetalleJuez";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /juez/{}/confirm-delete", id);
        model.addAttribute("item", service.get(jwt(session), id));
        return "juez/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /juez/{}/delete", id);

        try {
            service.delete(token, id);
            log.info("POST /juez/{}/delete OK", id);
            return "redirect:/jueces";

        } catch (ApiErrorException ex) {
            log.warn("POST /juez/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", service.get(token, id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el juez. Intentá nuevamente.");

            return "juez/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /juez/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("item", service.get(token, id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "juez/ConfirmarBorrado";
        }
    }
}