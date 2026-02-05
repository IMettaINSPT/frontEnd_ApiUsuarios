package com.tp.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    @GetMapping({"/menu"})
    public String menu() {
        log.info("GET /menu");
        return "menu";
    }
}