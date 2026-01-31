package com.tp.frontend.controller;

import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaRequest;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaUpdate;
import com.tp.frontend.service.BandaService;
import com.tp.frontend.service.PersonaDetenidaService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/personasDetenidas")
public class PersonasDetenidasController {

    private final PersonaDetenidaService personaService;
    private final BandaService bandaService;

    public PersonasDetenidasController(PersonaDetenidaService personaService, BandaService bandaService) {
        this.personaService = personaService;
        this.bandaService = bandaService;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("items", personaService.list(jwt(session)));
        return "personasDetenidas/ListaPersonasDetenidas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        model.addAttribute("form", new PersonaDetenidaRequest());
        model.addAttribute("bandas", bandaService.list(jwt(session)));
        return "personasDetenidas/CrearPersonaDetenida";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") PersonaDetenidaRequest form, HttpSession session) {
        personaService.create(jwt(session), form);
        return "redirect:/personasDetenidas";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = personaService.get(jwt(session), id);

        Long bandaId = (item.banda() != null) ? item.banda().id() : null;
        var update = new PersonaDetenidaUpdate(item.codigo(), item.nombre(), bandaId);

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        model.addAttribute("bandas", bandaService.list(jwt(session)));
        return "personasDetenidas/DetallePersonaDetenida";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") PersonaDetenidaUpdate update,
                         HttpSession session) {
        personaService.update(jwt(session), id, update);
        return "redirect:/personasDetenidas/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", personaService.get(jwt(session), id));
        return "personasDetenidas/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        personaService.delete(jwt(session), id);
        return "redirect:/personasDetenidas";
    }
}
