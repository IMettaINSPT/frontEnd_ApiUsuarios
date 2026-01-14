package com.tp.frontend.controller;

import com.tp.frontend.dto.Vigilante.VigilanteRequest;
import com.tp.frontend.dto.Vigilante.VigilanteUpdate;
import com.tp.frontend.service.VigilanteService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vigilantes")
public class VigilantesController {

    private final VigilanteService service;

    public VigilantesController(VigilanteService service) {
        this.service = service;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    // ADMIN/INVESTIGADOR
    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("items", service.list(jwt(session)));
        return "ListaVigilantes";
    }

    // ADMIN
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new VigilanteRequest());
        return "CrearVigilante";
    }

    // ADMIN
    @PostMapping
    public String create(@Valid @ModelAttribute("form") VigilanteRequest form,
                         BindingResult br,
                         HttpSession session) {
        if (br.hasErrors()) return "CrearVigilante";
        service.create(jwt(session), form);
        return "redirect:/vigilantes";
    }

    // ADMIN/INVESTIGADOR (ver detalle), ADMIN (editar)
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = service.getById(jwt(session), id);

        var update = new VigilanteUpdate();
        update.setCodigo(item.getCodigo());
        update.setEdad(item.getEdad());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        return "DetalleVigilante";
    }

    // ADMIN (editar)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") VigilanteUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("item", service.getById(jwt(session), id));
            return "DetalleVigilante";
        }
        service.update(jwt(session), id, update);
        return "redirect:/vigilantes/" + id;
    }

    // ADMIN
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.getById(jwt(session), id));
        return "ConfirmarBorrado";
    }

    // ADMIN
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        service.delete(jwt(session), id);
        return "redirect:/vigilantes";
    }

    // VIGILANTE
    @GetMapping("/me")
    public String me(HttpSession session, Model model) {
        model.addAttribute("item", service.me(jwt(session)));
        return "vigilantes/me";
    }
}
