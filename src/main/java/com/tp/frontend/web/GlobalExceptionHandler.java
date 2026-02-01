package com.tp.frontend.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ModelAndView forwardError(HttpServletRequest request, int status, Exception ex) {

        // 1) Genero un ID para rastrear en logs (opcional pero MUY útil)
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        // 2) Datos útiles del request
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String fullPath = (query == null) ? uri : (uri + "?" + query);

        // 3) Log con stacktrace (IMPORTANTE: pasar "ex" como último parámetro)
        log.error("[ERROR_ID={}] {} {} -> status={} | {}",
                errorId, method, fullPath, status, ex.toString(), ex);

        // 4) Seteo el status para el controller /error (sin mensaje técnico)
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, status);

        // (Opcional) Le paso el errorId a la vista para que el usuario lo mencione
        request.setAttribute("ERROR_ID", errorId);

        ModelAndView mv = new ModelAndView("forward:/error");
        mv.setStatus(HttpStatus.valueOf(status));
        return mv;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDenied(HttpServletRequest request, AccessDeniedException ex) {
        return forwardError(request, HttpStatus.FORBIDDEN.value(), ex);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ModelAndView backendDown(HttpServletRequest request, ResourceAccessException ex) {
        // backend caído / timeout / DNS
        return forwardError(request, HttpStatus.SERVICE_UNAVAILABLE.value(), ex);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ModelAndView httpError(HttpServletRequest request, HttpStatusCodeException ex) {
        // errores 4xx/5xx devueltos por el backend
        return forwardError(request, ex.getStatusCode().value(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView generic(HttpServletRequest request, Exception ex) {
        // cualquier otro error del frontend
        return forwardError(request, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex);
    }
}
