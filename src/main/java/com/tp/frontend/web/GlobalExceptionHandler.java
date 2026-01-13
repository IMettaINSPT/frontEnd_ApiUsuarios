package com.tp.frontend.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDenied(AccessDeniedException ex) {
        return error("Acceso denegado (403)",
                "No tenés permisos para acceder a esta funcionalidad.",
                HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ModelAndView backendDown(ResourceAccessException ex) {
        return error("Backend no disponible",
                "No se pudo conectar al backend. Verificá que esté levantado y la URL configurada.",
                503);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ModelAndView httpError(HttpStatusCodeException ex) {
        int code = ex.getStatusCode().value();

        if (code == 401) {
            // si querés, acá podrías redirigir a login
            return error("Sesión expirada (401)",
                    "Tu sesión expiró o no estás autenticado. Volvé a iniciar sesión.",
                    401);
        }
        if (code == 403) {
            return error("Acceso denegado (403)",
                    "No tenés permisos para realizar esa acción.",
                    403);
        }

        return error("Error HTTP " + code,
                "El backend respondió con un error. Código: " + code,
                code);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView generic(Exception ex) {
        return error("Error inesperado",
                ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado.",
                500);
    }

    private ModelAndView error(String title, String message, int status) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("title", title);
        mv.addObject("message", message);
        mv.addObject("status", status);
        return mv;
    }
}
