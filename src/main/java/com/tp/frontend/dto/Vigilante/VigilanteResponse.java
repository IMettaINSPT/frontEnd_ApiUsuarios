package com.tp.frontend.dto.Vigilante;

public class VigilanteResponse {
    private Long id;
    private String codigo;
    private Integer edad;

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public Integer getEdad() { return edad; }

    public void setId(Long id) { this.id = id; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setEdad(Integer edad) { this.edad = edad; }
}
