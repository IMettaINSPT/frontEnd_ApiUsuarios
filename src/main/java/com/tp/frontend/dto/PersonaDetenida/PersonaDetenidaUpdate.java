package com.tp.frontend.dto.PersonaDetenida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonaDetenidaUpdate (
        @NotBlank(message="El codigo es obligatoria")
     String codigo,
        @NotBlank(message="El nombre es obligatoria")
     String nombre,
     Long bandaId
){
}
