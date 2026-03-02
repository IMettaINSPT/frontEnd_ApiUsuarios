package com.tp.frontend.dto.Login;

public class LoginResponse {
    private String token; // Coherente con el Backend Hexagonal
    private String username;
    private String rol;
    private Long rolId;

    public LoginResponse() {}

    // Getters y Setters...
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }
}