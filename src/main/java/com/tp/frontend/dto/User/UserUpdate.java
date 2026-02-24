package com.tp.frontend.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdate {

    @Size(min = 4, max = 100, message = "La password debe tener al menos 4 caracteres")
    private String password;

    private Boolean enabled;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    // No lleva @NotNull porque solo es obligatorio si el rol es VIGILANTE
    private Long vigilanteId;

    public UserUpdate() {}

    // ===== getters / setters =====

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Long getVigilanteId() {
        return vigilanteId;
    }

    public void setVigilanteId(Long vigilanteId) {
        this.vigilanteId = vigilanteId;
    }
}