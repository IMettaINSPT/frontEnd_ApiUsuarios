package com.tp.frontend.controller;

import com.tp.frontend.dto.User.UserRequest;
import com.tp.frontend.dto.User.UserResponse;
import com.tp.frontend.dto.User.UserUpdate;
import com.tp.frontend.exception.ApiErrorException;
import com.tp.frontend.service.UserService;
import com.tp.frontend.service.VigilanteService;
import com.tp.frontend.support.ErrorBinder;
import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Controller
@RequestMapping("/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;
    private final VigilanteService vigilanteService;
    private final ErrorBinder errorBinder;

    public UsersController(UserService userService, VigilanteService vigilanteService, ErrorBinder errorBinder) {
        this.userService = userService;
        this.vigilanteService = vigilanteService;
        this.errorBinder = errorBinder;
    }

    private String jwt(HttpSession session) {
        return (String) session.getAttribute(SessionKeys.JWT);
    }

    /** Carga vigilantes disponibles con el NOMBRE que usa tu template: ${vigilantes} */
    private void cargarVigilantesDisponibles(Model model, String token) {
        var disponibles = vigilanteService.disponibles(token);
        model.addAttribute("vigilantes", disponibles); // ✅ coincide con CrearUsuario.html
        model.addAttribute("hayVigilantes", !disponibles.isEmpty());
    }

    private void addGlobalError(BindingResult br, String msg) {
        br.reject("global", msg);
    }

    // =========================
    // LISTA
    // =========================

    @GetMapping
    public String list(Model model,
                       HttpSession session,
                       @RequestParam(required = false) String msg,
                       @RequestParam(required = false) String err) {

        log.info("GET /users msg={} err={}", msg, err);

        if (msg != null) model.addAttribute("msg", msg);
        if (err != null) model.addAttribute("error", err);

        String token = jwt(session);

        try {
            model.addAttribute("users", userService.list(token));
        } catch (WebClientRequestException ex) {
            log.warn("GET /users transport error: {}", ex.getMessage());
            model.addAttribute("users", java.util.List.of());
            model.addAttribute("error", "No pudimos conectarnos al servidor. Intentá nuevamente.");
        } catch (ApiErrorException ex) {
            log.warn("GET /users ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());
            model.addAttribute("users", java.util.List.of());
            model.addAttribute("error", "No se pudo obtener el listado de usuarios.");
        }

        return "users/ListaUsuario";
    }

    // =========================
    // CREATE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        String token = jwt(session);
        log.info("GET /users/new");

        model.addAttribute("form", new UserRequest()); // ✅ coincide con CrearUsuario.html
        cargarVigilantesDisponibles(model, token);

        return "users/CrearUsuario";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("form") UserRequest form,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /users create username={} tipo={}", form.getUsername(), form.getTipo());

        // si no es VIGILANTE, anulamos vigilanteId (tal como ya estabas haciendo)
        if (!"VIGILANTE".equalsIgnoreCase(form.getTipo())) {
            form.setVigilanteId(null);
        }

        if (br.hasErrors()) {
            log.warn("POST /users create SSR validation errors={}", br.getErrorCount());
            cargarVigilantesDisponibles(model, token);
            return "users/CrearUsuario";
        }

        try {
            userService.create(form, token);
            log.info("POST /users create OK");
            return "redirect:/users?msg=Usuario creado correctamente";

        } catch (ApiErrorException ex) {
            log.warn("POST /users create ApiError status={} code={} msg={}",
                    ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            cargarVigilantesDisponibles(model, token);
            return "users/CrearUsuario";

        } catch (WebClientRequestException ex) {
            log.warn("POST /users create transport error: {}", ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            cargarVigilantesDisponibles(model, token);
            return "users/CrearUsuario";
        }
    }

    // =========================
    // DETALLE
    // =========================

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         Model model,
                         HttpSession session,
                         @RequestParam(required = false) String msg,
                         @RequestParam(required = false) String err) {

        log.info("GET /users/{} msg={} err={}", id, msg, err);

        if (msg != null) model.addAttribute("msg", msg);
        if (err != null) model.addAttribute("error", err);

        String token = jwt(session);

        try {
            UserResponse user = userService.get(id, token);
            model.addAttribute("user", user);
            model.addAttribute("update", new UserUpdate()); // password/ enabled

            return "users/DetalleUsuario";

        } catch (ApiErrorException ex) {
            log.warn("GET /users/{} ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            return "redirect:/users?err=No se pudo abrir el usuario solicitado";

        } catch (WebClientRequestException ex) {
            log.warn("GET /users/{} transport error: {}", id, ex.getMessage());
            return "redirect:/users?err=No pudimos conectarnos al servidor";
        }
    }

    // =========================
    // UPDATE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("update") UserUpdate update,
                         BindingResult br,
                         HttpSession session,
                         Model model) {

        String token = jwt(session);
        log.info("POST /users/{} update enabled={} passwordProvided={}",
                id, update.getEnabled(), update.getPassword() != null && !update.getPassword().isBlank());

        if (br.hasErrors()) {
            log.warn("POST /users/{} update SSR validation errors={}", id, br.getErrorCount());
            model.addAttribute("user", userService.get(id, token));
            return "users/DetalleUsuario";
        }

        // si password viene vacío, lo mandamos como null (para no “pisar”)
        if (update.getPassword() != null && update.getPassword().isBlank()) {
            update.setPassword(null);
        }

        try {
            userService.update(id, update, token);
            log.info("POST /users/{} update OK", id);
            return "redirect:/users/" + id + "?msg=Usuario actualizado";

        } catch (ApiErrorException ex) {
            log.warn("POST /users/{} update ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            errorBinder.bind(ex, br);
            model.addAttribute("user", userService.get(id, token));
            return "users/DetalleUsuario";

        } catch (WebClientRequestException ex) {
            log.warn("POST /users/{} update transport error: {}", id, ex.getMessage());
            addGlobalError(br, "No pudimos conectarnos al servidor. Intentá nuevamente.");
            model.addAttribute("user", userService.get(id, token));
            return "users/DetalleUsuario";
        }
    }

    // =========================
    // DELETE
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, Model model, HttpSession session) {
        String token = jwt(session);
        log.info("GET /users/{}/confirm-delete", id);

        try {
            model.addAttribute("user", userService.get(id, token));
            return "users/ConfirmarBorrado";
        } catch (Exception ex) {
            log.warn("GET /users/{}/confirm-delete failed: {}", id, ex.getMessage());
            return "redirect:/users?err=No se pudo abrir la confirmación de borrado";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String token = jwt(session);
        log.info("POST /users/{}/delete", id);

        try {
            userService.delete(id, token);
            log.info("POST /users/{}/delete OK", id);
            return "redirect:/users?msg=Usuario eliminado";

        } catch (ApiErrorException ex) {
            log.warn("POST /users/{}/delete ApiError status={} code={} msg={}",
                    id, ex.getHttpStatus(),
                    ex.getApiError() != null ? ex.getApiError().getCode() : null,
                    ex.getApiError() != null ? ex.getApiError().getMessage() : ex.getMessage());

            model.addAttribute("user", userService.get(id, token));
            model.addAttribute("deleteError",
                    ex.getApiError() != null && ex.getApiError().getMessage() != null
                            ? ex.getApiError().getMessage()
                            : "No se pudo eliminar el usuario. Intentá nuevamente.");
            return "users/ConfirmarBorrado";

        } catch (WebClientRequestException ex) {
            log.warn("POST /users/{}/delete transport error: {}", id, ex.getMessage());
            model.addAttribute("user", userService.get(id, token));
            model.addAttribute("deleteError", "No pudimos conectarnos al servidor. Intentá nuevamente.");
            return "users/ConfirmarBorrado";
        }
    }
}