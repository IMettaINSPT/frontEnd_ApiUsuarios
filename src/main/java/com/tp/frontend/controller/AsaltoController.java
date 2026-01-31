package com.tp.frontend.controller;

import com.tp.frontend.dto.Asalto.AsaltoRequest;
import com.tp.frontend.dto.Asalto.AsaltoUpdate;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Sucursal.SucursalResponse;
import com.tp.frontend.service.AsaltoService;
import com.tp.frontend.service.PersonaDetenidaService;
import com.tp.frontend.service.SucursalService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asaltos")
public class AsaltoController {

    private final AsaltoService asaltoService;
    private final SucursalService sucursalService;
    private final PersonaDetenidaService personaDetenidaService;

    public AsaltoController(AsaltoService asaltoService,
                             SucursalService sucursalService,
                             PersonaDetenidaService personaDetenidaService) {
        this.asaltoService = asaltoService;
        this.sucursalService = sucursalService;
        this.personaDetenidaService = personaDetenidaService;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    private Map<Long, SucursalResponse> sucursalesMap(String token) {
        return sucursalService.list(token).stream()
                .collect(Collectors.toMap(SucursalResponse::getId, Function.identity()));
    }

    private Map<Long, PersonaDetenidaResponse> personasMap(String token) {
        return personaDetenidaService.list(token).stream()
                .collect(Collectors.toMap(PersonaDetenidaResponse::id, Function.identity()));
    }

    // ===== LISTADO (sin filtros) =====
    @GetMapping
    public String list(HttpSession session, Model model) {
        String token = jwt(session);

        model.addAttribute("items", asaltoService.list(token));
        model.addAttribute("sucursalesMap", sucursalesMap(token));
        model.addAttribute("personasMap", personasMap(token));

        return "asaltos/ListaAsaltos";
    }

    // ===== REPORTE (con filtros) =====
    @GetMapping("/reporte")
    public String reporte(@RequestParam(required = false) Long sucursalId,
                          @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fecha,
                          @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate desde,
                          @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate hasta,
                          HttpSession session,
                          Model model) {

        String token = jwt(session);

        var resultados = asaltoService.search(token, sucursalId, fecha, desde, hasta);

        model.addAttribute("items", resultados);
        model.addAttribute("sucursales", sucursalService.list(token)); // combo filtro
        model.addAttribute("sucursalesMap", sucursalesMap(token));
        model.addAttribute("personasMap", personasMap(token));

        // para mantener filtros en pantalla
        model.addAttribute("filterSucursalId", sucursalId);
        model.addAttribute("filterFecha", fecha);
        model.addAttribute("filterDesde", desde);
        model.addAttribute("filterHasta", hasta);

        return "asaltos/ReporteAsaltos";
    }

    // ===== CREATE =====
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        String token = jwt(session);

        model.addAttribute("form", new AsaltoRequest());
        model.addAttribute("sucursales", sucursalService.list(token));
        model.addAttribute("personas", personaDetenidaService.list(token));

        return "asaltos/CrearAsalto";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("form") AsaltoRequest form, HttpSession session) {
        asaltoService.create(jwt(session), form);
        return "redirect:/asaltos";
    }

    // ===== DETAIL/UPDATE =====
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);

        var item = asaltoService.get(token, id);

        var update = new AsaltoUpdate();
        update.setFechaAsalto(item.getFechaAsalto());
        update.setSucursalId(item.getSucursalId());
        update.setPersonaDetenidaId(item.getPersonaDetenidaId());

        model.addAttribute("item", item);
        model.addAttribute("update", update);

        model.addAttribute("sucursales", sucursalService.list(token));
        model.addAttribute("personas", personaDetenidaService.list(token));

        model.addAttribute("sucursalesMap", sucursalesMap(token));
        model.addAttribute("personasMap", personasMap(token));

        return "asaltos/DetalleAsalto";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("update") AsaltoUpdate update, HttpSession session) {
        asaltoService.update(jwt(session), id, update);
        return "redirect:/asaltos/" + id;
    }

    // ===== DELETE =====
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("item", asaltoService.get(jwt(session), id));
        return "asaltos/ConfirmarBorrado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        asaltoService.delete(jwt(session), id);
        return "redirect:/asaltos";
    }
}
