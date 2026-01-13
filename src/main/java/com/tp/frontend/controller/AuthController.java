package com.tp.frontend.controller;

import com.tp.frontend.dto.Login.*;
import com.tp.frontend.service.AuthService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final HttpSessionSecurityContextRepository securityContextRepo = new HttpSessionSecurityContextRepository();

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ============================
    // LOGIN VIEW
    // ============================
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String logout,
                        @RequestParam(required = false) String err,
                        Model model) {

        if (logout != null) model.addAttribute("msg", "Gracias por visitar el sitio de la Policía Federal.");
        if (err != null) model.addAttribute("error", err);

        return "login";
    }

    // ============================
    // DO LOGIN (manual, contra backend)
    // ============================
    @PostMapping("/do-login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        // MDC: nunca loguees password en claro
        MDC.put("user", username);
        log.info("Intento de login user={} passwordLength={}", username, password != null ? password.length() : 0);

        try {
            // 1) login (backend) -> token
            LoginResponse login = authService.login(username, password);
            String jwt = login != null ? login.getAccessToken() : null;

            if (jwt == null || jwt.isBlank()) {
                log.warn("Login sin token user={}", username);
                model.addAttribute("error", "No se pudo iniciar sesión. Intente nuevamente.");
                return "login";
            }

            // 2) me (backend) -> username + rol
            MeResponse me = authService.me(jwt);
            if (me == null || me.getUsername() == null || me.getRol() == null) {
                log.error("Respuesta /me inválida para user={}. me={}", username, me);
                model.addAttribute("error", "No se pudo validar la sesión. Intente nuevamente.");
                return "login";
            }

            String meUsername = me.getUsername();
            String role = me.getRol(); // e.g. ADMIN / INVESTIGADOR / VIGILANTE

            // 3) Crear Authentication para Spring Security
            var auth = new UsernamePasswordAuthenticationToken(
                    meUsername,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            // 4) Persistir SecurityContext en sesión (CLAVE para sobrevivir al redirect)
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepo.saveContext(context, request, response);

            // 5) Guardar datos de negocio en sesión (para llamadas al backend con JWT)
            session.setAttribute(SessionKeys.JWT, jwt);
            session.setAttribute(SessionKeys.USERNAME, meUsername);
            session.setAttribute(SessionKeys.ROLE, role);

            log.info("Login OK user={} role={}", meUsername, role);

            // 6) Redirect por rol
            if ("VIGILANTE".equalsIgnoreCase(role)) {
                return "redirect:/vigilantes/me";
            }
            return "redirect:/menu";

        } catch (HttpStatusCodeException ex) {

            // credenciales inválidas
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST ||
                    ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {

                log.warn("Login inválido user={} status={}", username, ex.getStatusCode());
                model.addAttribute("error", "Usuario o contraseña incorrectos.");
                return "login";
            }

            // otros códigos (500, etc.)
            log.error("Error HTTP login user={} status={} body={}",
                    username, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

            model.addAttribute("error", "No se pudo iniciar sesión. Intente más tarde.");
            return "login";

        } catch (ResourceAccessException ex) {

            log.error("Backend no disponible login user={}", username, ex);
            model.addAttribute("error", "No se puede conectar al backend. Verifique que esté activo.");
            return "login";

        } catch (Exception ex) {

            log.error("Error inesperado login user={}", username, ex);
            model.addAttribute("error", "Ocurrió un error inesperado al iniciar sesión.");
            return "login";

        } finally {
            MDC.remove("user");
        }
    }

}
