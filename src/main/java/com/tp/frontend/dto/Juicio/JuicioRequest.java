package com.tp.frontend.dto.Juicio;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class JuicioRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    // Ej: "CONDENADO", "ABSULTO", "EN_PROCESO" (según tu backend)
    private String resultado;

    private Long juezId;
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
