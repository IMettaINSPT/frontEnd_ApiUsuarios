package com.tp.frontend.dto.Juicio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class JuicioRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="La fecha es obligatoria")
    private LocalDate fecha;

    // Ej: "CONDENADO", "ABSULTO", "EN_PROCESO" (según tu backend)
    @NotBlank(message="El resultado es obligatoria")
    private String resultado;

    @NotNull(message="El juez es obligatoria")
    private Long juezId;

    @NotNull(message="La persona detenida es obligatoria")
    private Long personaDetenidaId;


    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public Long getJuezId() { return juezId; }
    public void setJuezId(Long juezId) { this.juezId = juezId; }

    public Long getPersonaDetenidaId() { return personaDetenidaId; }
    public void setPersonaDetenidaId(Long personaDetenidaId) { this.personaDetenidaId = personaDetenidaId; }
}
