package com.tp.frontend.dto.PersonaDetenida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonaDetenidaUpdate (
        @NotBlank(message="El codigo es obligatorio")
          String codigo,
        @NotBlank(message="El nombre es obligatorio")
          String nombre,
        @NotBlank(message="El apellido es obligatorio")
          String apellido,

          Long bandaId
){
}
