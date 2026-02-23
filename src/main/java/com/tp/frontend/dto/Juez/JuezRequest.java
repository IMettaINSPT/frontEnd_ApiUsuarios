package com.tp.frontend.dto.Juez;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JuezRequest {
    @NotBlank(message="La clave juzgado es obligatoria")
    private String claveJuzgado;

    @NotBlank(message="El nombre es obligatorio")
    private String nombre;

    @NotBlank(message="El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "Los años de servicio son obligatorios")
    @Min(value = 1, message = "Los años de servicio mínimo es 1")
    private Integer anosServicio;

    public JuezRequest() {}

    public String getClaveJuzgado() { return claveJuzgado; }
    public void setClaveJuzgado(String claveJuzgado) { this.claveJuzgado = claveJuzgado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getAnosServicio() { return anosServicio; }
    public void setAnosServicio(Integer anosServicio) { this.anosServicio = anosServicio; }
}