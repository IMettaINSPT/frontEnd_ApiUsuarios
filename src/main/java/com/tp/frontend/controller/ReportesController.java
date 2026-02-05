package com.tp.frontend.controller;

import com.tp.frontend.dto.Asalto.AsaltoResponse;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Sucursal.SucursalResponse;
import com.tp.frontend.service.AsaltoService;
import com.tp.frontend.service.PersonaDetenidaService;
import com.tp.frontend.service.SucursalService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    private static final Logger log = LoggerFactory.getLogger(ReportesController.class);

    private final AsaltoService asaltoService;
    private final SucursalService sucursalService;
    private final PersonaDetenidaService personaDetenidaService;

    public ReportesController(AsaltoService asaltoService,
                              SucursalService sucursalService,
                              PersonaDetenidaService personaDetenidaService) {
        this.asaltoService = asaltoService;
        this.sucursalService = sucursalService;
        this.personaDetenidaService = personaDetenidaService;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        String token = jwt(session);
        log.info("GET /reportes");

        List<AsaltoResponse> ultimosAsaltos = asaltoService.list(token).stream()
                .sorted(Comparator.comparing(AsaltoResponse::getFechaAsalto).reversed())
                .limit(5)
                .toList();

        Map<Long, SucursalResponse> sucursalesMap =
                sucursalService.list(token).stream()
                        .collect(Collectors.toMap(SucursalResponse::getId, Function.identity()));

        Map<Long, PersonaDetenidaResponse> personasMap =
                personaDetenidaService.list(token).stream()
                        .collect(Collectors.toMap(PersonaDetenidaResponse::id, Function.identity()));

        model.addAttribute("ultimosAsaltos", ultimosAsaltos);
        model.addAttribute("sucursalesMap", sucursalesMap);
        model.addAttribute("personasMap", personasMap);

        return "reportes/IndexReportes";
    }
}