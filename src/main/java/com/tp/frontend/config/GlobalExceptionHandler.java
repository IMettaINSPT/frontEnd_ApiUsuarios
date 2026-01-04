package com.tp.frontend.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAccessException.class)
    public ModelAndView backendDown(ResourceAccessException ex) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("title", "Backend no disponible");
        mv.addObject("message", "No se pudo conectar al backend. Verificá que esté levantado y la URL configurada.");
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView generic(Exception ex) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("title", "Error inesperado");
        mv.addObject("message", ex.getMessage());
        return mv;
    }
}
