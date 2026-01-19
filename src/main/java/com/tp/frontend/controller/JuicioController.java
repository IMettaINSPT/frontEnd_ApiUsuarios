package com.tp.frontend.controller;

import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import com.tp.frontend.service.JuicioService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/juicios")
public class JuicioController {

    private final JuicioService service;

    public JuicioController(JuicioService service) {
        this.service = service;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("items", service.list(jwt(session)));
        return "juicios/ListaJuicios";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new JuicioRequest());
        model.addAttribute("resultados", new String[]{"EN_PROCESO", "CONDENADO", "ABSULTO"});
        return "juicios/CrearJuicio";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") JuicioRequest form, HttpSession session) {
        service.create(jwt(session), form);
        return "redirect:/juicios";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = service.get(jwt(session), id);

        var update = new JuicioUpdate();
        update.setFecha(item.getFecha());
        update.setResultado(item.getResultado());
        update.setJuezId(item.getJuezId());
        update.setPersonaDetenidaId(item.getPersonaDetenidaId());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        model.addAttribute("resultados", new String[]{"EN_PROCESO", "CONDENADO", "ABSULTO"});

        return "juicios/DetalleJuicio";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") JuicioUpdate update,
                         HttpSession session) {
        service.update(jwt(session), id, update);
        return "redirect:/juicios/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.get(jwt(session), id));
        return "juicios/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        service.delete(jwt(session), id);
        return "redirect:/juicios";
    }
}
