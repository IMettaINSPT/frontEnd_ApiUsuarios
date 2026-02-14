package com.tp.frontend.dto.Vigilante;

public class VigilanteResponse {
    private Long id;
    private String codigo;
    private Integer edad;
    private Long contratoId;
    private Long numContrato;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Long contratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }

    public Long getNumContrato() { return numContrato; }
    public void setNumContrato(Long numContrato) { this.numContrato = numContrato; }
}
