package com.tp.frontend.dto.Juicio;

import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.Asalto.AsaltoResponse;
import java.time.LocalDate;

public class JuicioResponse {
    private Long id;
    private String expediente;
    private LocalDate fechaJuicio;


    private Boolean condenado;


    private String situacionPenal;
    private String detallePena;

    private JuezResponse juez;
    private AsaltoResponse asalto;
    private PersonaDetenidaResponse persona;

    public JuicioResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExpediente() { return expediente; }
    public void setExpediente(String expediente) { this.expediente = expediente; }

    public LocalDate getFechaJuicio() { return fechaJuicio; }
    public void setFechaJuicio(LocalDate fechaJuicio) { this.fechaJuicio = fechaJuicio; }


    public Boolean getCondenado() { return condenado; }

    /**
     * Al recibir el valor del Backend, actualizamos automáticamente
     * el texto descriptivo para la vista.
     */
    public void setCondenado(Boolean condenado) {
        this.condenado = condenado;
        if (condenado != null) {
            this.situacionPenal = condenado ? "CONDENADO" : "ABSUELTO";
        }
    }

    public String getSituacionPenal() {
        if (situacionPenal == null && condenado != null) {
            return condenado ? "CONDENADO" : "ABSUELTO";
        }
        return situacionPenal;
    }

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