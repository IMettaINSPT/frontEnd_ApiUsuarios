package com.tp.frontend.dto.Juez;

import java.util.List;
import com.tp.frontend.dto.Juicio.JuicioResponse;



public class JuezResponse {
    private Long id;
    private String claveJuzgado;
    private String nombre;
    private String apellido;
    private Integer anosServicio;
    private Integer cantidadJuicios;
    private List<JuicioResponse> juicios;

    public JuezResponse() {
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaveJuzgado() { return claveJuzgado; }
    public void setClaveJuzgado(String claveJuzgado) { this.claveJuzgado = claveJuzgado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getAnosServicio() { return anosServicio; }
    public void setAnosServicio(Integer anosServicio) { this.anosServicio = anosServicio; }

    public Integer getCantidadJuicios() {return cantidadJuicios;}
    public void setCantidadJuicios(Integer cantidadJuicios) {this.cantidadJuicios = cantidadJuicios;}

    // Getter y Setter para la lista
    public List<JuicioResponse> getJuicios() {
        return juicios;
    }
    public void setJuicios(List<JuicioResponse> juicios) {
        this.juicios = juicios;
    }

}
