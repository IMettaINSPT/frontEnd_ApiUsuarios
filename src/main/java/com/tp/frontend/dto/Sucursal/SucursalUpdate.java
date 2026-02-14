package com.tp.frontend.dto.Sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SucursalUpdate(
@NotBlank(message="El codigo es obligatorio")
 String codigo,
@NotBlank(message="El domicilio es obligatorio")
 String domicilio,

@NotNull(message="El nro de empleados es obligatorio")
 Integer nroEmpleados,

@NotNull(message="El banco es obligatorio")
 Long bancoId
){}