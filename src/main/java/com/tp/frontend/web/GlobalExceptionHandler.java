package com.tp.frontend.web;

import com.tp.frontend.dto.Error.ApiError;
import com.tp.frontend.exception.ApiErrorException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice(annotations = Controller.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiErrorException.class)
    public String handleApiErrorException(ApiErrorException ex, HttpServletRequest request, Model model) {
        ApiError apiError = ex.getApiError();
        String rid = UUID.randomUUID().toString();

        log.warn("FRONT ApiErrorException rid={} path={} status={} code={} msg={}",
                rid,
                request.getRequestURI(),
                ex.getHttpStatus(),
                apiError != null ? apiError.getCode() : null,
                apiError != null ? apiError.getMessage() : ex.getMessage()
        );

        model.addAttribute("rid", rid);
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());
//siempre int para que no rompa
        model.addAttribute("status", ex.getHttpStatus());
        model.addAttribute("errorMessage",
                apiError != null && apiError.getMessage() != null ? apiError.getMessage() : ex.getMessage());

        model.addAttribute("apiError", apiError);

        return "error";
    }

    @ExceptionHandler(WebClientRequestException.class)
    public String handleWebClientRequestException(WebClientRequestException ex, HttpServletRequest request, Model model) {
        String rid = UUID.randomUUID().toString();

        log.error("FRONT WebClientRequestException rid={} path={} msg={}",
                rid, request.getRequestURI(), ex.getMessage(), ex);

        model.addAttribute("rid", rid);
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());

        model.addAttribute("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("errorMessage", "No se pudo conectar con el backend (red/timeout). Probá de nuevo en unos segundos.");

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, HttpServletRequest request, Model model) {
        String rid = UUID.randomUUID().toString();

        log.error("FRONT GenericException rid={} path={} exClass={} msg={}",
                rid, request.getRequestURI(), ex.getClass().getName(), ex.getMessage(), ex);

        model.addAttribute("rid", rid);
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());

        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorMessage", "Error interno del servidor.");

        return "error";
    }
}
