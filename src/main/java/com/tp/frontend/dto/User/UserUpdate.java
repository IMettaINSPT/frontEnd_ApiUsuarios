package com.tp.frontend.dto.User;

public class UserUpdate {
    /**
     * Mirror del backend: UsuarioUpdateRequest
     */
    private String password;
    private Boolean enabled;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
