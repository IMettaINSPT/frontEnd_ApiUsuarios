package com.tp.frontend.dto.PersonaDetenida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PersonaDetenidaRequest {
    @NotBlank(message="El codigo es obligatoria")
    private String codigo;

    @NotBlank(message="El nombre es obligatoria")
    private String nombre;

    @NotNull(message="La banda es obligatoria")
    private Long bandaId; // opcional

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getBandaId() { return bandaId; }
    public void setBandaId(Long bandaId) { this.bandaId = bandaId; }
}
