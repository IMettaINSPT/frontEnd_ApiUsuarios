package com.tp.frontend.dto.Vigilante;

/**
 * Mirror del backend: com.tp.backend.dto.vigilante.VigilanteResponse
 */
public class VigilanteResponse {

    private Long id;
    private String codigo;
    private int edad;

    public VigilanteResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}
