package com.tp.frontend.dto.PersonaDetenida;

public class PersonaDetenidaRequest {
    private String codigo;
    private String nombre;
    private Long bandaId; // opcional

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getBandaId() { return bandaId; }
    public void setBandaId(Long bandaId) { this.bandaId = bandaId; }
}
