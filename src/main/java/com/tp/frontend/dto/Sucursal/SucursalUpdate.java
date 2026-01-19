package com.tp.frontend.dto.Sucursal;

public class SucursalUpdate {
    private String codigo;
    private String domicilio;
    private Integer nroEmpleados;
    private Long bancoId;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public Integer getNroEmpleados() { return nroEmpleados; }
    public void setNroEmpleados(Integer numeroEmpleados) { this.nroEmpleados = numeroEmpleados; }

    public Long getBancoId() { return bancoId; }
    public void setBancoId(Long bancoId) { this.bancoId = bancoId; }
}
