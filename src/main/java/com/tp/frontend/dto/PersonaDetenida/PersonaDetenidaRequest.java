package com.tp.frontend.dto.PersonaDetenida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PersonaDetenidaRequest {
    @NotBlank(message="El codigo es obligatorio")
    private String codigo;

    @NotBlank(message="El nombre es obligatorio")
    private String nombre;

    @NotBlank(message="El apellido es obligatorio")
    private String apellido;

    private Long bandaId; // opcional

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Long getBandaId() { return bandaId; }
    public void setBandaId(Long bandaId) { this.bandaId = bandaId; }
}
