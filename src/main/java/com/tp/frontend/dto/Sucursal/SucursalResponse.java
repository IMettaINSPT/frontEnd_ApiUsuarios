package com.tp.frontend.dto.Sucursal;

public class SucursalResponse {
    private Long id;
    private String codigo;
    private String domicilio;
    private Integer nroEmpleados;
    private Long bancoId;
    private String bancoCodigo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

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

    public String getBancoCodigo() {
        return bancoCodigo;
    }

    public void setBancoCodigo(String bancoCodigo) {
        this.bancoCodigo = bancoCodigo;
    }
}
