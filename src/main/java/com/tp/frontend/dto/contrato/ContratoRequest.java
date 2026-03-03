package com.tp.frontend.dto.contrato;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ContratoRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
    private LocalDate fechaContrato;

    private LocalDate fechaFin;

    @NotNull(message="EL uso o no de tobiller es obligatorio de indicar")
    private Boolean conArma;



    @NotNull(message="La sucursal es obligatoria")
    private Long sucursalId;

    @NotNull(message="El vigilante es obligatorio")
    private Long vigilanteId;

    public LocalDate getFechaContrato() { return fechaContrato; }
    public void setFechaContrato(LocalDate fechaContrato) { this.fechaContrato = fechaContrato; }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getConArma() { return conArma; }
    public void setConArma(Boolean conArma) { this.conArma = conArma; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public Long getVigilanteId() { return vigilanteId; }
    public void setVigilanteId(Long vigilanteId) { this.vigilanteId = vigilanteId; }


}
