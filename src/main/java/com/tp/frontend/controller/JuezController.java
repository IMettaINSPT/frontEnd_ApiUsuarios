package com.tp.frontend.controller;

import com.tp.frontend.dto.Juez.JuezRequest;
import com.tp.frontend.dto.Juez.JuezUpdate;
import com.tp.frontend.service.JuezService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jueces")
public class JuezController {

    private final JuezService service;

    public JuezController(JuezService service) {
        this.service = service;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("items", service.list(jwt(session)));
        return "juez/ListaJueces";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new JuezRequest());
        return "juez/CrearJuez";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") JuezRequest form, HttpSession session) {
        service.create(jwt(session), form);
        return "redirect:/jueces";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = service.get(jwt(session), id);

        var update = new JuezUpdate();
        update.setCodigo(item.getCodigo());
        update.setNombre(item.getNombre());
        update.setApellido(item.getApellido());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        return "juez/DetalleJuez";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") JuezUpdate update,
                         HttpSession session,
                         Model model) {
        service.update(jwt(session), id, update);
        return "redirect:/juez/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.get(jwt(session), id));
        return "juez/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        service.delete(jwt(session), id);
        return "redirect:/jueces";
    }
}
