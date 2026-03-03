package com.tp.frontend.controller;

import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.User.UserResponse; // 🟢 Cambio: Usamos UserResponse
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.AuthService;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.List;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final HttpSessionSecurityContextRepository securityContextRepo = new HttpSessionSecurityContextRepository();

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ModelAttribute("form")
    public LoginRequest form() {
        return new LoginRequest();
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String logout,
                        @RequestParam(required = false) String err,
                        Model model,
                        Authentication authentication) {

        log.info("GET /login logout={} err={}", logout, err);

        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            log.info("GET /login - Usuario ya autenticado, redirigiendo");
            var roles = authentication.getAuthorities();
            boolean esVigilante = roles.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VIGILANTE"));
            if (esVigilante) {
                return "redirect:/vigilantes/me";
            }

            return "redirect:/dashboard";
        }

        if (logout != null) model.addAttribute("logout", true);
        if (err != null) model.addAttribute("err", err);

        return "login";
    }

    @PostMapping("/auth/login")
    public String doLogin(@Valid @ModelAttribute("form") LoginRequest form,
                          BindingResult br,
                          HttpSession session,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        String username = safeUsername(form);
        MDC.put("user", username);

        if (br.hasErrors()) {
            return "login";
        }

        try {
            // 1. Login (Obtenemos token)
            LoginResponse login = authService.login(form);
            String jwt = login != null ? login.getToken() : null; // 🟢 Cambio: .getToken() según tu record

            if (jwt == null || jwt.isBlank()) {
                br.reject("auth.error", "No se pudo iniciar sesión.");
                return "login";
            }

            // 2. Perfil (Usamos UserResponse con el campo 'rol' unificado)
            UserResponse user = authService.me(jwt);
            if (user == null || user.getUsername() == null || user.getRol() == null) {
                br.reject("auth.error", "No se pudo validar el perfil.");
                return "login";
            }

            String meUsername = user.getUsername();
            String role = user.getRol(); // 🟢 Coherencia: campo 'rol' del Back

            // 3. Crear Autoridades con ROLE_
            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            var auth = new UsernamePasswordAuthenticationToken(
                    user, // 🟢 Guardamos el objeto completo como Principal
                    null,
                    List.of(authority)
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepo.saveContext(context, request, response);

            // 4. Sesión HTTP
            session.setAttribute(SessionKeys.JWT, jwt);
            session.setAttribute(SessionKeys.USERNAME, meUsername);
            session.setAttribute(SessionKeys.ROLE, role);

            log.info("POST /auth/login OK user={} role={}", meUsername, role);

            // Redirección basada en rol
            if ("VIGILANTE".equalsIgnoreCase(role)) return "redirect:/vigilantes/me";
            return "redirect:/dashboard";

        } catch (ApiErrorException ex) {
            br.reject("auth.error", "Usuario o contraseña incorrectos.");
            return "login";
        } catch (WebClientRequestException ex) {
            br.reject("auth.error", "Backend no disponible.");
            return "login";
        } catch (Exception ex) {
            log.error("Error inesperado", ex);
            br.reject("auth.error", "Error inesperado al iniciar sesión.");
            return "login";
        } finally {
            MDC.remove("user");
        }
    }

    private String safeUsername(LoginRequest form) {
        return form != null && form.getUsername() != null ? form.getUsername() : "null";
    }
}