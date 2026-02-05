package com.tp.frontend.dto.Banco;

import jakarta.validation.constraints.NotBlank;

public record BancoUpdate(
    @NotBlank(message="El codigo es obligatorio")
     String codigo,

    @NotBlank(message="El domicilio es obligatorio")
     String domicilioCentral
){}