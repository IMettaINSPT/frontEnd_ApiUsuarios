package com.tp.frontend.dto.contrato;

import java.time.LocalDate;

public class ContratoResponse {

    private Long id;
    private LocalDate fechaContrato;
    private Boolean conArma;

    private Long sucursalId;
    private Long vigilanteId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaContrato() { return fechaContrato; }
    public void setFechaContrato(LocalDate fechaContrato) { this.fechaContrato = fechaContrato; }

    public Boolean getConArma() { return conArma; }
    public void setConArma(Boolean conArma) { this.conArma = conArma; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public Long getVigilanteId() { return vigilanteId; }
    public void setVigilanteId(Long vigilanteId) { this.vigilanteId = vigilanteId; }
}
