package com.tp.frontend.dto.Asalto;

import java.time.LocalDate;

public class AsaltoResponse {
    private Long id;
    private LocalDate fechaAsalto;
    private Long sucursalId;
    private Long personaDetenidaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaAsalto() { return fechaAsalto; }
    public void setFechaAsalto(LocalDate fechaAsalto) { this.fechaAsalto = fechaAsalto; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public Long getPersonaDetenidaId() { return personaDetenidaId; }
    public void setPersonaDetenidaId(Long personaDetenidaId) { this.personaDetenidaId = personaDetenidaId; }
}
