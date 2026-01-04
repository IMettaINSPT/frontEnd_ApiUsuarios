package com.tp.frontend.controller;

import com.tp.frontend.service.AuthService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String logout,
                            HttpSession session) {

            session.invalidate();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();

        return "login";
    }

    @PostMapping("/do-login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        System.out.println(">>> ENTRO al POST /login (frontend)");

        try{
              String token = authService.loginAndGetToken(username, password);

            if (token == null || token.isBlank()) {
                model.addAttribute("error", "Usuario o contraseña incorrectos");
                return "login";
            }

        // 1) Guardar JWT (para llamar al backend)
        session.setAttribute(SessionKeys.JWT, token);

        } catch (Exception ex) {
            model.addAttribute("error", "Error llamando al backend: " + ex.getMessage());
            return "login";
        }
        // 2) Marcar autenticación en Spring Security y PERSISTIRLA en sesión
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, List.of());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

// 🔥 CLAVE: guardar el contexto en la sesión (para que sobreviva al redirect)
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
        return "redirect:/home";
    }
}
