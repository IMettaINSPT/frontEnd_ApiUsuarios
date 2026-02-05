package com.tp.frontend.controller;

import com.tp.frontend.dto.Vigilante.VigilanteRequest;
import com.tp.frontend.dto.Vigilante.VigilanteUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.VigilanteService;
import com.tp.frontend.support.ErrorBinder;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Controller
@RequestMapping("/vigilantes")
public class VigilantesController {

    private static final Logger log = LoggerFactory.getLogger(VigilantesController.class);

    private final VigilanteService service;
    private final ErrorBinder errorBinder;

    public VigilantesController(VigilanteService service, ErrorBinder errorBinder) {
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
        log.info("GET /vigilantes");
        model.addAttribute("items", service.list(jwt(session)));
        return "vigilantes/ListaVigilantes";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        log.info("GET /vigilantes/new");
        model.addAttribute("form", new VigilanteRequest());
        return "vigilantes/CrearVigilante";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") VigilanteRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        log.info("POST /vigilantes create form={}", form);

        if (br.hasErrors()) {
            log.warn("POST /vigilantes create SSR validation errors={}", br.getErrorCount());
            return "vigilantes/CrearVigilante";
        }

        try {
            service.create(jwt(session), form);
            log.info("POST /vigilantes create OK");
            return "redirect:/vigilantes";

        } catch (ApiErrorException ex) {
            log.warn("POST /vigilantes create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            return "vigilantes/CrearVigilante";

        } catch (WebClientRequestException ex) {
            log.warn("POST /vigilantes create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "vigilantes/CrearVigilante";

        } catch (Exception ex) {
            log.error("POST /vigilantes create unexpected", ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /vigilantes/{}", id);

        var item = service.getById(jwt(session), id);
        var update = new VigilanteUpdate(item.getCodigo(), item.getEdad());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        return "vigilantes/DetalleVigilante";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") VigilanteUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        log.info("POST /vigilantes/{} update={}", id, update);

        if (br.hasErrors()) {
            log.warn("POST /vigilantes/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("item", service.getById(jwt(session), id));
            return "vigilantes/DetalleVigilante";
        }

        try {
            service.update(jwt(session), id, update);
            log.info("POST /vigilantes/{} update OK", id);
            return "redirect:/vigilantes/" + id;

        } catch (ApiErrorException ex) {
            log.warn("POST /vigilantes/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("item", service.getById(jwt(session), id));
            return "vigilantes/DetalleVigilante";

        } catch (WebClientRequestException ex) {
            log.warn("POST /vigilantes/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("item", service.getById(jwt(session), id));
            return "vigilantes/DetalleVigilante";

        } catch (Exception ex) {
            log.error("POST /vigilantes/{} update unexpected", id, ex);
            throw ex;
        }
    }

    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("GET /vigilantes/{}/confirm-delete", id);
        model.addAttribute("item", service.getById(jwt(session), id));
        return "vigilantes/ConfirmarBorrado";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        log.info("POST /vigilantes/{}/delete", id);

        try {
            service.delete(jwt(session), id);
            log.info("POST /vigilantes/{}/delete OK", id);
            return "redirect:/vigilantes";

        } catch (ApiErrorException ex) {
            log.warn("POST /vigilantes/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("item", service.getById(jwt(session), id));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el vigilante. Intentá nuevamente.");
            return "vigilantes/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /vigilantes/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("item", service.getById(jwt(session), id));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "vigilantes/ConfirmarBorrado";
        }
    }

    @GetMapping("/me")
    public String me(HttpSession session, Model model) {
        log.info("GET /vigilantes/me");
        model.addAttribute("item", service.me(jwt(session)));
        return "vigilantes/me";
    }
}