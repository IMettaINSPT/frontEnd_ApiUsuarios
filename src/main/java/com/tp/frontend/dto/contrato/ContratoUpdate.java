package com.tp.frontend.dto.contrato;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ContratoUpdate (

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
     LocalDate fechaContrato,

    @NotNull(message="EL uso o no de arma es obligatoria de indicar")
     Boolean conArma,

    @NotNull(message="La sucursal es obligatoria")
     Long sucursalId,

    @NotNull(message="El vigilante es obligatoria")
     Long vigilanteId
){}