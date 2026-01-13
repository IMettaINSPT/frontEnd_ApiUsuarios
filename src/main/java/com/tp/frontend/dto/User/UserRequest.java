package com.tp.frontend.dto.User;

public class UserRequest {
    private String username;
    private String password;
    /**
     * Backend espera: tipo = ADMIN | INVESTIGADOR | VIGILANTE
     */
    private String tipo;
    private Long vigilanteId;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    // Alias por compatibilidad (si algún template viejo sigue usando "rol")
    public String getRol() { return tipo; }
    public void setRol(String rol) { this.tipo = rol; }
    public void setRole(String rol) { this.tipo = rol; }

    public Long getVigilanteId() {
        return vigilanteId;
    }

    public void setVigilanteId(Long vigilanteId) {
        this.vigilanteId = vigilanteId;
    }
}
