package com.tp.frontend.dto.contrato;

import java.time.LocalDate;

public class ContratoResponse {

    private Long id;
    private String codigo;
    private LocalDate fechaContrato;
    private Boolean conArma;
    private Long sucursalId;
    private String sucursalCodigo;
    private Long vigilanteId;
    private LocalDate fechaFin;

    public boolean isActivo() {
        return fechaFin == null || fechaFin.isAfter(LocalDate.now());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDate getFechaContrato() { return fechaContrato; }
    public void setFechaContrato(LocalDate fechaContrato) { this.fechaContrato = fechaContrato; }

    public Boolean getConArma() { return conArma; }
    public void setConArma(Boolean conArma) { this.conArma = conArma; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public String getSucursalCodigo() {
        return sucursalCodigo;
    }
    public void setSucursalCodigo(String sucursalCodigo) {
        this.sucursalCodigo = sucursalCodigo;
    }

    public Long getVigilanteId() { return vigilanteId; }
    public void setVigilanteId(Long vigilanteId) { this.vigilanteId = vigilanteId; }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
