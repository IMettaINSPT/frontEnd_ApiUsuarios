package com.tp.frontend.dto.Asalto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class AsaltoRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
    private LocalDate fechaAsalto;

    @NotNull(message="La sucursal es obligatorio")
    private Long sucursalId;

    // List<Long> para soportar selección múltiple
    // Usamos @NotEmpty porque @NotNull en una lista no valida si está vacía
    @NotEmpty(message="Debe seleccionar al menos una persona detenida")
    private List<Long> personaDetenidaIds;

    public LocalDate getFechaAsalto() { return fechaAsalto; }
    public void setFechaAsalto(LocalDate fechaAsalto) { this.fechaAsalto = fechaAsalto; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public List<Long> getPersonaDetenidaIds() { return personaDetenidaIds; }
    public void setPersonaDetenidaIds(List<Long> personaDetenidaIds) { this.personaDetenidaIds = personaDetenidaIds; }

}
