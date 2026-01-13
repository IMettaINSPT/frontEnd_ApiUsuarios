package com.tp.frontend.web;

public enum AppRole {
    ADMIN, INVESTIGADOR, VIGILANTE;

    public static AppRole fromBackend(String rol) {
        if (rol == null) return INVESTIGADOR;
        return AppRole.valueOf(rol.trim().toUpperCase());
    }

    public String asSpringRole() {
        return "ROLE_" + this.name();
    }
}
