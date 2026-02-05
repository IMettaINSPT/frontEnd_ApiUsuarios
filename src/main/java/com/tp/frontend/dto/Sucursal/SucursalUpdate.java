package com.tp.frontend.dto.Sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SucursalUpdate(
@NotBlank(message="El codigo es obligatoria")
 String codigo,
@NotBlank(message="El domicilio es obligatoria")
 String domicilio,

@NotNull(message="El nro de empleados es obligatoria")
 Integer nroEmpleados,

@NotNull(message="El banco es obligatoria")
 Long bancoId
){}