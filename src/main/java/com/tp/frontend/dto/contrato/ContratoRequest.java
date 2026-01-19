package com.tp.frontend.dto.contrato;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ContratoRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaContrato;

    private Boolean conArma;

    private Long sucursalId;
    private Long vigilanteId;

    public LocalDate getFechaContrato() { return fechaContrato; }
    public void setFechaContrato(LocalDate fechaContrato) { this.fechaContrato = fechaContrato; }

    public Boolean getConArma() { return conArma; }
    public void setConArma(Boolean conArma) { this.conArma = conArma; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public Long getVigilanteId() { return vigilanteId; }
    public void setVigilanteId(Long vigilanteId) { this.vigilanteId = vigilanteId; }
}
