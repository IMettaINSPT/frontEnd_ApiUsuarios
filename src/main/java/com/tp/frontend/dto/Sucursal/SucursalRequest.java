package com.tp.frontend.dto.Sucursal;

public class SucursalRequest {
    private String codigo;
    private String domicilio;
    private Integer numeroEmpleados;
    private Long bancoId;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public Integer getNumeroEmpleados() { return numeroEmpleados; }
    public void setNumeroEmpleados(Integer numeroEmpleados) { this.numeroEmpleados = numeroEmpleados; }

    public Long getBancoId() { return bancoId; }
    public void setBancoId(Long bancoId) { this.bancoId = bancoId; }
}
