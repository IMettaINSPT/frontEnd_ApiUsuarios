package com.tp.frontend.dto.Juez;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JuezRequest {
    @NotBlank(message="El codigo es obligatoria")
    private String codigo;

    @NotBlank(message="El nombre es obligatoria")
    private String nombre;

    @NotBlank(message="El apellido es obligatoria")
    private String apellido;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
