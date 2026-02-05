package com.tp.frontend.dto.Vigilante;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VigilanteUpdate (

    @NotBlank(message = "El código es obligatorio")
     String codigo,

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "La edad mínima es 18")
     Integer edad
){}