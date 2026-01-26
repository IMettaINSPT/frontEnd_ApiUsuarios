package com.tp.frontend.controller;

import com.tp.frontend.dto.Banco.BancoRequest;
import com.tp.frontend.dto.Banco.BancoUpdate;
import com.tp.frontend.service.BancoService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bancos")
public class BancoController {

    private final BancoService service;

    public BancoController(BancoService service) {
        this.service = service;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("items", service.list(jwt(session)));
        return "banco/ListaBancos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BancoRequest());
        return "banco/CrearBanco";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") BancoRequest form, HttpSession session) {
        service.create(jwt(session), form);
        return "redirect:/bancos";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = service.get(jwt(session), id);

        var update = new BancoUpdate();
        update.setCodigo(item.getCodigo());
        update.setDomicilioCentral(item.getDomicilioCentral());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        return "banco/DetalleBanco";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") BancoUpdate update,
                         HttpSession session) {
        service.update(jwt(session), id, update);
        return "redirect:/bancos/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.get(jwt(session), id));
        return "banco/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        service.delete(jwt(session), id);
        return "redirect:/bancos";
    }
}
