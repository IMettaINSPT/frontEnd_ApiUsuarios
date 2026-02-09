package com.tp.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.frontend.dto.Dashboard.DashboardSummaryResponse;
import com.tp.frontend.service.DashboardService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService service;
    private final ObjectMapper objectMapper;

    public DashboardController(DashboardService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGADOR')")
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) throws Exception {
        var summary = service.summary(jwt(session));

        model.addAttribute("summary", summary);
        // opcional: si lo querés usar inline en el HTML
        model.addAttribute("summaryJson", objectMapper.writeValueAsString(summary));

        // ⚠️ Asegurate que exista: templates/dashboard/Dashboard.html
        // Si tu archivo es templates/dashboard/dashboard.html, cambiá a "dashboard/dashboard"
        return "dashboard/dashboard";
    }

    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGADOR')")
    @GetMapping(value = "/dashboard/summary", produces = "application/json")
    @ResponseBody
    public DashboardSummaryResponse dashboardSummary(HttpSession session) {
        return service.summary(jwt(session));
    }
}