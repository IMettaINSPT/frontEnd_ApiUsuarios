package com.tp.frontend.dto.Banda;

import jakarta.validation.constraints.NotNull;

public record BandaUpdate (
    @NotNull(message="El numero de banda es obligatoria")
     Integer numeroBanda,

    @NotNull(message="El numero de miembros es obligatoria")
     Integer numeroMiembros){}