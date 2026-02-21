package com.tp.frontend.dto.Juez;

import jakarta.validation.constraints.NotBlank;

public record JuezUpdate (
    @NotBlank(message="El codigo es obligatorio")
     String codigo,

    @NotBlank(message="El nombre es obligatorio")
     String nombre,

    @NotBlank(message="El apellido es obligatorio")
     String apellido
){}