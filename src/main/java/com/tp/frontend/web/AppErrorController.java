package com.tp.frontend.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        // Tomo el status si existe (pero NO lo mostramos al usuario)
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = 500;
        if (statusObj != null) {
            try { status = Integer.parseInt(statusObj.toString()); } catch (Exception ignored) {}
        }

        // Mensaje genérico para el usuario (sin filtrar backend)
        model.addAttribute("title", "Ocurrió un problema");
        model.addAttribute("message", "No fue posible procesar la solicitud en este momento. Intentá nuevamente más tarde.");

        // (Opcional) Podés cambiar el mensaje según status sin mostrar el número:
        if (status == HttpStatus.FORBIDDEN.value()) {
            model.addAttribute("message", "No tenés permisos para acceder a esta funcionalidad.");
        } else if (status == HttpStatus.UNAUTHORIZED.value()) {
            model.addAttribute("message", "Tu sesión expiró o no estás autenticado. Volvé a iniciar sesión.");
        } else if (status == 404) {
            model.addAttribute("message", "La página solicitada no existe o fue movida.");
        }
        Object errorId = request.getAttribute("ERROR_ID");
        model.addAttribute("errorId", errorId);
        return "error/error"; // templates/error.html
    }
}
