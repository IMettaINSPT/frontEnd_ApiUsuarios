package com.tp.frontend.dto.Asalto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record AsaltoUpdate(

        @NotNull(message="El código es obligatorio")
        String codigo,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull(message="La fecha es obligatoria")
         LocalDate fechaAsalto,

        @NotNull(message="La sucursal es obligatorio")
        Long sucursalId,

        // Usamos List<Long> y @NotEmpty para asegurar que al menos haya uno
        @NotEmpty(message="Debe seleccionar al menos una persona detenida")
        List<Long> personaDetenidaIds
){}