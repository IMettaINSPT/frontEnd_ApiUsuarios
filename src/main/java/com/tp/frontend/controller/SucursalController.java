package com.tp.frontend.controller;

import com.tp.frontend.dto.Banco.BancoResponse;
import com.tp.frontend.dto.Sucursal.SucursalRequest;
import com.tp.frontend.dto.Sucursal.SucursalUpdate;
import com.tp.frontend.service.BancoService;
import com.tp.frontend.service.SucursalService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;
    private final BancoService bancoService;

    public SucursalController(SucursalService sucursalService, BancoService bancoService) {
        this.sucursalService = sucursalService;
        this.bancoService = bancoService;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private List<BancoResponse> bancos(String token) {
        return bancoService.list(token);
    }

    private Map<Long, BancoResponse> bancosMap(String token) {
        return bancos(token).stream().collect(Collectors.toMap(BancoResponse::getId, Function.identity()));
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        String token = jwt(session);
        model.addAttribute("items", sucursalService.list(token));
        model.addAttribute("bancosMap", bancosMap(token)); // para mostrar codigo + domicilioCentral
        return "sucursales/ListaSucursales";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);
        model.addAttribute("form", new SucursalRequest());
        model.addAttribute("bancos", bancos(token)); // combo
        return "sucursales/CrearSucursal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") SucursalRequest form, HttpSession session) {
        sucursalService.create(jwt(session), form);
        return "redirect:/sucursales";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        var item = sucursalService.get(token, id);

        var update = new SucursalUpdate();
        update.setCodigo(item.getCodigo());
        update.setDomicilio(item.getDomicilio());
        update.setNroEmpleados(item.getNroEmpleados());
        update.setBancoId(item.getBancoId());

        model.addAttribute("item", item);
        model.addAttribute("update", update);
        model.addAttribute("bancos", bancos(token));       // combo en edición
        model.addAttribute("bancosMap", bancosMap(token)); // mostrar datos banco

        return "sucursales/DetalleSucursal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("update") SucursalUpdate update, HttpSession session) {
        sucursalService.update(jwt(session), id, update);
        return "redirect:/sucursales/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", sucursalService.get(jwt(session), id));
        return "sucursales/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        sucursalService.delete(jwt(session), id);
        return "redirect:/sucursales";
    }
}
