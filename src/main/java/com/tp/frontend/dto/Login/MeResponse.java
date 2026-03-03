package com.tp.frontend.dto.Login;

public class MeResponse {

    private String username;
    private String rol;

    public MeResponse() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
