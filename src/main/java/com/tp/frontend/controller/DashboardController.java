package com.tp.frontend.controller;

import com.tp.frontend.service.DashboardService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGADOR')")
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        log.info("GET /dashboard");
        var summary = service.summary(jwt(session));
        model.addAttribute("summary", summary);
        return "dashboard/Dashboard";
    }
}