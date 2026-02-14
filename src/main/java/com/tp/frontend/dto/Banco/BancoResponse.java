package com.tp.frontend.dto.Banco;
import java.util.List;
import com.tp.frontend.dto.Sucursal.SucursalResponse; //  importar DTO de sucursal

public class BancoResponse {
    private Long id;
    private String codigo;
    private String domicilioCentral;
    private Long sucursalId;
    private String sucursalCodigo;
    // Lista
    private List<SucursalResponse> sucursales;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDomicilioCentral() { return domicilioCentral; }
    public void setDomicilioCentral(String domicilioCentral) { this.domicilioCentral = domicilioCentral; }

    public Long getSucursalId() {
        return sucursalId;
    }
    public void setSucursalId(Long bancoId) {
        this.sucursalId = sucursalId;
    }

    public String getSucursalCodigo() {
        return sucursalCodigo;
    }
    public void setSucursalCodigo(String sucursalCodigo) {
        this.sucursalCodigo = sucursalCodigo;
    }

    // Getter y Setter para la lista
    public List<SucursalResponse> getSucursales() {
        return sucursales;
    }
    public void setSucursales(List<SucursalResponse> sucursales) {
        this.sucursales = sucursales;
    }


}
