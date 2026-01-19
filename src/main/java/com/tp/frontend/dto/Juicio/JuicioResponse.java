package com.tp.frontend.dto.Juicio;

import java.time.LocalDate;

public class JuicioResponse {
    private Long id;
    private LocalDate fecha;
    private String resultado;

    private Long juezId;
    private Long personaDetenidaId;

    // si tu backend devuelve campos extra, los podés agregar (por ej juezCodigo, personaCodigo, etc.)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public Long getJuezId() { return juezId; }
    public void setJuezId(Long juezId) { this.juezId = juezId; }

    public Long getPersonaDetenidaId() { return personaDetenidaId; }
    public void setPersonaDetenidaId(Long personaDetenidaId) { this.personaDetenidaId = personaDetenidaId; }
}
