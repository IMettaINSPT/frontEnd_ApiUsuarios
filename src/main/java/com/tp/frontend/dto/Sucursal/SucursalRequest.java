package com.tp.frontend.dto.Sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SucursalRequest {
    @NotBlank(message="El codigo es obligatorio")
    private String codigo;

    @NotBlank(message="El domicilio es obligatorio")
    private String domicilio;

    @NotNull(message="El número de empleados es obligatorio")
    private Integer nroEmpleados;

    @NotNull(message="El banco es obligatorio")
    private Long bancoId;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public Integer getNroEmpleados() {
        return nroEmpleados;
    }

    public void setNroEmpleados(Integer nroEmpleados) {
        this.nroEmpleados = nroEmpleados;
    }

    public Long getBancoId() {
        return bancoId;
    }

    public void setBancoId(Long bancoId) {
        this.bancoId = bancoId;
    }
}
