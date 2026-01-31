package com.tp.frontend.controller;

import com.tp.frontend.dto.contrato.ContratoRequest;
import com.tp.frontend.dto.contrato.ContratoUpdate;
import com.tp.frontend.service.ContratoService;
import com.tp.frontend.service.SucursalService;
import com.tp.frontend.service.VigilanteService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contratos")
public class ContratosController {

    private final ContratoService service;
    private final SucursalService sucursalService;
    private final VigilanteService vigilanteService;

    public ContratosController(ContratoService service, SucursalService sucursalService, VigilanteService vigilanteService) {
        this.service = service;
        this.sucursalService = sucursalService;
        this.vigilanteService = vigilanteService;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long sucursalId,
                       @RequestParam(required = false) Long vigilanteId,
                       HttpSession session,
                       Model model) {

        var token = jwt(session);

        if (sucursalId != null) {
            model.addAttribute("items", service.listPorSucursal(token, sucursalId));
        } else if (vigilanteId != null) {
            model.addAttribute("items", service.listPorVigilante(token, vigilanteId));
        } else {
            model.addAttribute("items", service.list(token));
        }

        model.addAttribute("filterSucursalId", sucursalId);
        model.addAttribute("filterVigilanteId", vigilanteId);

        return "contratos/ListaContratos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session)  {
        var form = new ContratoRequest();
        form.setConArma(Boolean.FALSE);
        String jwt = (String) session.getAttribute(SessionKeys.JWT);


        model.addAttribute("form", form);

        //sucursal
        var sucdisponibles = sucursalService.list(jwt);
        model.addAttribute("sucursalesDisponibles", sucdisponibles);
        model.addAttribute("haySucursalesDisponibles", !sucdisponibles.isEmpty());

        // vigilantes
        var vigdisponibles = vigilanteService.disponibles(jwt);
        model.addAttribute("vigilantesDisponibles", vigdisponibles);
        model.addAttribute("hayVigilantesDisponibles", !vigdisponibles.isEmpty());


        return "contratos/CrearContrato";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") ContratoRequest form, HttpSession session) {
        // si viene null por checkbox, lo defaulteamos
        if (form.getConArma() == null) form.setConArma(Boolean.FALSE);

        service.create(jwt(session), form);
        return "redirect:/contratos";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = service.get(jwt(session), id);

        var update = new ContratoUpdate();
        update.setFechaContrato(item.getFechaContrato());
        update.setConArma(item.getConArma() != null ? item.getConArma() : Boolean.FALSE);
        update.setSucursalId(item.getSucursalId());
        update.setVigilanteId(item.getVigilanteId());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        return "contratos/DetalleContrato";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") ContratoUpdate update,
                         HttpSession session) {
        if (update.getConArma() == null) update.setConArma(Boolean.FALSE);

        service.update(jwt(session), id, update);
        return "redirect:/contratos/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", service.get(jwt(session), id));
        return "contratos/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        service.delete(jwt(session), id);
        return "redirect:/contratos";
    }
}
