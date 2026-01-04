package com.tp.frontend.controller;

import com.tp.frontend.dto.UserDTO;
import com.tp.frontend.service.UserService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model,
                       HttpSession session,
                       @RequestParam(required = false) String msg,
                       @RequestParam(required = false) String err) {

        if (msg != null) model.addAttribute("msg", msg);
        if (err != null) model.addAttribute("error", err);

        String jwt = (String) session.getAttribute(SessionKeys.JWT);

        try {
            List<UserDTO> users = userService.getAllUsers(jwt);
            model.addAttribute("users", users);
            return "users";

        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 401) {
                return "redirect:/login?err=Sesion%20expirada";
            }
            if (ex.getStatusCode().value() == 403) {
                model.addAttribute("error", "No tenés permisos para ver usuarios (requiere ADMIN).");
                model.addAttribute("users", List.of());
                return "users";
            }
            model.addAttribute("error", "Error al obtener usuarios.");
            model.addAttribute("users", List.of());
            return "users";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {

        String jwt = (String) session.getAttribute(SessionKeys.JWT);

        try {
            userService.deleteUser(id, jwt);
            return "redirect:/users?msg=Usuario%20eliminado";

        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 403) {
                return "redirect:/users?err=No%20tenes%20permisos%20para%20eliminar";
            }
            if (ex.getStatusCode().value() == 401) {
                return "redirect:/login?err=Sesion%20expirada";
            }
            return "redirect:/users?err=Error%20al%20eliminar";
        }
    }
}
