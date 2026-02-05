package com.tp.frontend.dto.Juicio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record JuicioUpdate (

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
     LocalDate fecha,

    // Ej: "CONDENADO", "ABSULTO", "EN_PROCESO" (según tu backend)
    @NotBlank(message="El resultado es obligatoria")
     String resultado,

    @NotNull(message="El juez es obligatoria")
     Long juezId,

    @NotNull(message="La persona detenida es obligatoria")
     Long personaDetenidaId
){}