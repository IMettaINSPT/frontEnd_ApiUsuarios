package com.tp.frontend.dto.Asalto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class AsaltoRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
    private LocalDate fechaAsalto;

    @NotNull(message="La sucursal es obligatorio")
    private Long sucursalId;

    @NotNull(message="La persona detenida es obligatorio")
    private Long personaDetenidaId;

    public LocalDate getFechaAsalto() { return fechaAsalto; }
    public void setFechaAsalto(LocalDate fechaAsalto) { this.fechaAsalto = fechaAsalto; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public Long getPersonaDetenidaId() { return personaDetenidaId; }
    public void setPersonaDetenidaId(Long personaDetenidaId) { this.personaDetenidaId = personaDetenidaId; }
}
