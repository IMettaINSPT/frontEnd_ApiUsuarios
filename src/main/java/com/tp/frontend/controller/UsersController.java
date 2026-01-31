package com.tp.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.frontend.client.VigilantesApiClient;
import com.tp.frontend.service.UserService;
import com.tp.frontend.service.VigilanteService;
import com.tp.frontend.web.SessionKeys;
import com.tp.frontend.dto.User.UserResponse;
import com.tp.frontend.dto.User.UserRequest;
import com.tp.frontend.dto.User.UserUpdate;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private ObjectMapper objectMapper;

    private final UserService userService;
    private final VigilanteService vigilanteService;

    public UsersController(UserService userService, VigilanteService vigilanteService) {
        this.userService = userService;
        this.vigilanteService = vigilanteService;
    }

    // =========================
    // LISTADO
    // =========================
    @GetMapping
    public String list(Model model,
                       HttpSession session,
                       @RequestParam(required = false) String msg,
                       @RequestParam(required = false) String err) {

        if (msg != null) model.addAttribute("msg", msg);
        if (err != null) model.addAttribute("error", err);

        String jwt = (String) session.getAttribute(SessionKeys.JWT);

        model.addAttribute("users", userService.list(jwt));

        return "users/ListaUsuario";
    }

    // =========================
    // FORM ALTA (ADMIN)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        model.addAttribute("user", new UserRequest());

        // Para el caso de rol VIGILANTE, el front debe poder elegir un Vigilante disponible.
        String jwt = (String) session.getAttribute(SessionKeys.JWT);
        var disponibles = vigilanteService.disponibles(jwt);
        model.addAttribute("vigilantesDisponibles", disponibles);
        model.addAttribute("hayVigilantesDisponibles", !disponibles.isEmpty());

        return "users/CrearUsuario";
    }

    // =========================
    // CREAR (ADMIN)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@ModelAttribute("user") UserRequest user,
                         HttpSession session) {

        String jwt = (String) session.getAttribute(SessionKeys.JWT);

        // Limpieza defensiva: vigilanteId solo aplica si rol == VIGILANTE
        if (!"VIGILANTE".equalsIgnoreCase(user.getTipo())) {
            user.setVigilanteId(null);
        }

        userService.create(user, jwt);

        return "redirect:/users?msg=Usuario creado correctamente";
    }

    // =========================
    // DETALLE / EDICIÓN
    // =========================
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         Model model,
                         HttpSession session,
                         @RequestParam(required = false) String msg,
                         @RequestParam(required = false) String err) {

        if (msg != null) model.addAttribute("msg", msg);
        if (err != null) model.addAttribute("error", err);

        String jwt = (String) session.getAttribute(SessionKeys.JWT);

        UserResponse user = userService.get(id, jwt);
        model.addAttribute("user", user);

        // Form de update (backend solo permite password/enabled)
        UserUpdate update = new UserUpdate();
        update.setEnabled(user.isEnabled());
        model.addAttribute("update", update);

        // Para ADMIN: si edita y cambia rol a VIGILANTE, necesita lista de opciones.
        var disponibles = vigilanteService.disponibles(jwt);
        model.addAttribute("vigilantesDisponibles", disponibles);
        model.addAttribute("hayVigilantesDisponibles", !disponibles.isEmpty());

        return "users/DetalleUsuario";
    }

    // =========================
    // ACTUALIZAR (ADMIN)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("update") UserUpdate update,
                         HttpSession session) {

        String jwt = (String) session.getAttribute(SessionKeys.JWT);

        userService.update(id, update, jwt);

        return "redirect:/users/" + id + "?msg=Usuario actualizado";
    }

    // =========================
    // ELIMINAR (ADMIN)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpSession session) {

        String jwt = (String) session.getAttribute(SessionKeys.JWT);
        userService.delete(id, jwt);

        return "redirect:/users?msg=Usuario eliminado";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/confirm-delete")
    public String confirmDelete(@PathVariable Long id, Model model, HttpSession session) {
        String jwt = (String) session.getAttribute(SessionKeys.JWT);
        model.addAttribute("user", userService.get(id, jwt));
        return "users/ConfirmarBorrado";
    }

}
