package com.tp.frontend.dto.PersonaDetenida;

public class PersonaDetenidaResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private Long bandaId;
    private String bandaNombre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getBandaId() { return bandaId; }
    public void setBandaId(Long bandaId) { this.bandaId = bandaId; }

    public String getBandaNombre() { return bandaNombre; }
    public void setBandaNombre(String bandaNombre) { this.bandaNombre = bandaNombre; }
}
