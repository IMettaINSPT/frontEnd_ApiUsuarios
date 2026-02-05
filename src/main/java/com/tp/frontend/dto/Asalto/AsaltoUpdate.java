package com.tp.frontend.dto.Asalto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record AsaltoUpdate(

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
     LocalDate fechaAsalto,

    @NotNull(message="La sucursal es obligatorio")
     Long sucursalId,

    @NotNull(message="La persona detenida es obligatorio")
     Long personaDetenidaId
){}