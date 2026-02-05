package com.tp.frontend.dto.Juez;

import jakarta.validation.constraints.NotBlank;

public record JuezUpdate (
    @NotBlank(message="El codigo es obligatoria")
     String codigo,

    @NotBlank(message="El nombre es obligatoria")
     String nombre,

    @NotBlank(message="El apellido es obligatoria")
     String apellido
){}