package com.tp.frontend.controller;

import com.tp.frontend.dto.Banda.BandaRequest;
import com.tp.frontend.dto.Banda.BandaUpdate;
import com.tp.frontend.service.BandaService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bandas")
public class BandasController {

    private final BandaService service;

    public BandasController(BandaService service) {
        this.service = service;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("items", service.list(jwt(session)));
        return "bandas/ListaBandas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BandaRequest());
        return "bandas/CrearBanda";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") BandaRequest form, HttpSession session) {
        service.create(jwt(session), form);
        return "redirect:/bandas";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = service.get(jwt(session), id);

        var update = new BandaUpdate();
        update.setNumeroBanda(item.getNumeroBanda());
        update.setNumeroMiembros(item.getNumeroMiembros());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        return "bandas/DetalleBanda";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") BandaUpdate update,
                         HttpSession session) {
        service.update(jwt(session), id, update);
        return "redirect:/bandas/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.get(jwt(session), id));
        return "bandas/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        service.delete(jwt(session), id);
        return "redirect:/bandas";
    }
}
