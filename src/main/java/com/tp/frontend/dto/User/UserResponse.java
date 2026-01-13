package com.tp.frontend.dto.User;

public class UserResponse {
    private Long id;
    private String username;
    private String rol;      // ✅ igual que el backend
    private boolean enabled; // si lo usás

    public UserResponse() {}

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRol() { return rol; }
    public boolean isEnabled() { return enabled; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setRol(String rol) { this.rol = rol; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
