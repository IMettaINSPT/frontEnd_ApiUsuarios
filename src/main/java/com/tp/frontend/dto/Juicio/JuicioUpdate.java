package com.tp.frontend.dto.Juicio;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record JuicioUpdate (

        @NotBlank(message = "El número de expediente es obligatorio")
        String expediente,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull(message = "La fecha del juicio es obligatoria")
        LocalDate fechaJuicio,

        @NotBlank(message = "La situación penal es obligatoria")
        String situacionPenal,

        @NotNull(message = "El juez es obligatorio")
        Long juezId,

        @NotNull(message = "El asalto/delito es obligatorio")
        Long asaltoId,

        @NotNull(message = "La persona detenida es obligatoria")
        Long personaDetenidaId,

        // Campos opcionales para cuando es "Condenado"
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fechaInicioCondena,

        @Min(value = 1, message = "El tiempo de condena debe ser al menos 1 mes")
        Integer tiempoCondenaMeses
){}