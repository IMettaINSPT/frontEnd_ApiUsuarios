package com.tp.frontend.dto.Juez;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JuezUpdate (
    @NotBlank(message="La clave juzgado es obligatoria")
     String claveJuzgado,

    @NotBlank(message="El nombre es obligatorio")
     String nombre,

    @NotBlank(message="El apellido es obligatorio")
     String apellido,

    @NotNull(message = "Los años de servicio son obligatorios")
    @Min(value = 1, message = "Los años de servicio mínimo es 1")
    Integer anosServicio

){}