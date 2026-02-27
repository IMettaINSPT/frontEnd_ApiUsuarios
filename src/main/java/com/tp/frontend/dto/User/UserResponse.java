package com.tp.frontend.dto.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private Long id;
    private String codigo;
    private String username;
    private String rol;
    private boolean enabled;
    private Long vigilanteId;
    private String vigilanteCodigo;

    public UserResponse() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // El cambio aquí permite que si la API no envía estos campos (ej. para un Admin),
    // el Frontend no intente procesar referencias nulas.
    public Long getVigilanteId() { return vigilanteId; }
    public void setVigilanteId(Long vigilanteId) { this.vigilanteId = vigilanteId; }

    public String getVigilanteCodigo() { return vigilanteCodigo; }
    public void setVigilanteCodigo(String vigilanteCodigo) { this.vigilanteCodigo = vigilanteCodigo; }
}