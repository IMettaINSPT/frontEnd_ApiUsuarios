package com.tp.frontend.controller;

import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.Login.MeResponse;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    /**
     * Asegura que SIEMPRE exista "form" para templates/login.html (th:object="${form}").
     * Evita: "Neither BindingResult nor plain target object for bean name 'form'..."
     */
    @ModelAttribute("form")
    public LoginRequest form() {
        return new LoginRequest();
    }

    /**
     * Compatibilidad: tu app tal vez redirige a /login por Spring Security.
     * Renderiza templates/login.html.
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String logout,
                        @RequestParam(required = false) String err,
                        Model model) {

        log.info("GET /login logout={} err={}", logout, err);

        // Si querés mostrar un mensaje arriba (tu HTML actual no lo muestra),
        // podés agregarlo luego en la vista.
        if (logout != null) {
            // mejor como global error/info si querés:
            // model.addAttribute("msg", "Gracias por visitar...");
            model.addAttribute("logout", true);
        }

        if (err != null) {
            model.addAttribute("err", err);
        }

        return "login";
    }

    /**
     * Endpoint que usa tu login.html: th:action="@{/auth/login}"
     * POST con DTO + BindingResult para que el HTML muestre errores.
     */
    @PostMapping("/auth/login")
    public String doLogin(@Valid @ModelAttribute("form") LoginRequest form,
                          BindingResult br,
                          HttpSession session,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        String username = safeUsername(form);
        MDC.put("user", username);
        log.info("POST /auth/login user={} passwordLength={}",
                username,
                form != null && form.getPassword() != null ? form.getPassword().length() : 0);

        // Validación SSR (@Valid). Si falla, vuelve a login sin pegarle al backend.
        if (br.hasErrors()) {
            log.info("POST /auth/login SSR validation errors={}", br.getErrorCount());
            return "login";
        }

        try {
            LoginResponse login = authService.login(form); // <<< tu AuthService debe tener esta sobrecarga
            String jwt = login != null ? login.getAccessToken() : null;

            if (jwt == null || jwt.isBlank()) {
                log.warn("POST /auth/login sin token user={}", username);
                br.reject("auth.error", "No se pudo iniciar sesión. Intente nuevamente.");
                return "login";
            }

            MeResponse me = authService.me(jwt);
            if (me == null || me.getUsername() == null || me.getRol() == null) {
                log.error("POST /auth/login /me inválido user={} me={}", username, me);
                br.reject("auth.error", "No se pudo validar la sesión. Intente nuevamente.");
                return "login";
            }

            String meUsername = me.getUsername();
            String role = me.getRol();

            var auth = new UsernamePasswordAuthenticationToken(
                    meUsername,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepo.saveContext(context, request, response);

            session.setAttribute(SessionKeys.JWT, jwt);
            session.setAttribute(SessionKeys.USERNAME, meUsername);
            session.setAttribute(SessionKeys.ROLE, role);

            log.info("POST /auth/login OK user={} role={}", meUsername, role);

            if ("VIGILANTE".equalsIgnoreCase(role)) return "redirect:/vigilantes/me";
            return "redirect:/dashboard";

        } catch (ApiErrorException ex) {
            log.warn("POST /auth/login ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            // Para tu login.html (usa #fields.globalErrors())
            br.reject("auth.error", "Usuario o contraseña incorrectos.");
            // opcional por si querés debug:
            model.addAttribute("apiError", ex.getApiError());
            return "login";

        } catch (WebClientRequestException ex) {
            log.error("POST /auth/login backend no disponible user={}", username, ex);
            br.reject("auth.error", "No se puede conectar al backend. Verifique que esté activo.");
            return "login";

        } catch (Exception ex) {
            log.error("POST /auth/login unexpected user={}", username, ex);
            br.reject("auth.error", "Ocurrió un error inesperado al iniciar sesión.");
            return "login";

        } finally {
            MDC.remove("user");
        }
    }

    /**
     * Compatibilidad: si en algún lado todavía posteás a /do-login, lo redirigimos al nuevo flujo.
     * (Podés borrarlo cuando ya no lo use nadie.)
     */
    @PostMapping("/do-login")
    public String doLoginCompat(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        log.info("POST /do-login (compat) user={} passwordLength={}", username, password != null ? password.length() : 0);

        LoginRequest form = new LoginRequest();
        form.setUsername(username);
        form.setPassword(password);

        // Reutiliza el POST principal (sin @Valid acá; ya tenés constraints en HTML/JS)
        return doLogin(form, new org.springframework.validation.BeanPropertyBindingResult(form, "form"),
                session, model, request, response);
    }

    private String safeUsername(LoginRequest form) {
        try {
            return form != null && form.getUsername() != null ? form.getUsername() : "null";
        } catch (Exception e) {
            return "unknown";
        }
    }
}