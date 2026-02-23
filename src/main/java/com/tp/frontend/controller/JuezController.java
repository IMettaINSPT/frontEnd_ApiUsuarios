package com.tp.frontend.controller;

import com.tp.frontend.dto.Juez.JuezRequest;
import com.tp.frontend.dto.Juez.JuezUpdate;
import com.tp.frontend.dto.Juez.JuezResponse;
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

import java.util.List;

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
            return "juez/CrearJuez";
        }

        try {
            service.create(jwt(session), form);
            return "redirect:/jueces";
        } catch (ApiErrorException ex) {
            errorBinder.bind(ex, br);
            return "juez/CrearJuez";
        } catch (WebClientRequestException ex) {
            addGlobalError(br, "Error de conexión.");
            return "juez/CrearJuez";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /juez/{}", id);

        JuezResponse item = service.get(token, id);

        JuezUpdate update = new JuezUpdate();
        update.setClaveJuzgado(item.getClaveJuzgado());
        update.setNombre(item.getNombre());
        update.setApellido(item.getApellido());
        update.setAnosServicio(item.getAnosServicio());

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
            return "redirect:/jueces";
        } catch (ApiErrorException ex) {
            errorBinder.bind(ex, br);
            model.addAttribute("item", service.get(token, id));
            return "juez/DetalleJuez";
        } catch (WebClientRequestException ex) {
            addGlobalError(br, "Error de conexión.");
            model.addAttribute("item", service.get(token, id));
            return "juez/DetalleJuez";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.get(jwt(session), id));
        return "juez/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        try {
            service.delete(token, id);
            return "redirect:/jueces";
        } catch (ApiErrorException ex) {
            model.addAttribute("item", service.get(token, id));
            return "juez/ConfirmarBorrado";
        }
    }
}