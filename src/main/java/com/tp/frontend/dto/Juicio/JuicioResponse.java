package com.tp.frontend.dto.Juicio;

import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Asalto.AsaltoResponse;
import java.time.LocalDate;

public class JuicioResponse {
    private Long id;
    private String expediente;      // Coincide con j.expediente
    private LocalDate fechaJuicio;  // Coincide con j.fechaJuicio
    private String situacionPenal;  // Coincide con j.situacionPenal
    private String detallePena;     // Coincide con j.detallePena

    // Objetos anidados para que funcione la navegación j.juez.apellido, etc.
    private JuezResponse juez;
    private AsaltoResponse asalto;
    private PersonaDetenidaResponse persona;

    public JuicioResponse() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExpediente() { return expediente; }
    public void setExpediente(String expediente) { this.expediente = expediente; }

    public LocalDate getFechaJuicio() { return fechaJuicio; }
    public void setFechaJuicio(LocalDate fechaJuicio) { this.fechaJuicio = fechaJuicio; }

    public String getSituacionPenal() { return situacionPenal; }
    public void setSituacionPenal(String situacionPenal) { this.situacionPenal = situacionPenal; }

    public String getDetallePena() { return detallePena; }
    public void setDetallePena(String detallePena) { this.detallePena = detallePena; }

    public JuezResponse getJuez() { return juez; }
    public void setJuez(JuezResponse juez) { this.juez = juez; }

    public AsaltoResponse getAsalto() { return asalto; }
    public void setAsalto(AsaltoResponse asalto) { this.asalto = asalto; }

    public PersonaDetenidaResponse getPersona() { return persona; }
    public void setPersona(PersonaDetenidaResponse persona) { this.persona = persona; }
}