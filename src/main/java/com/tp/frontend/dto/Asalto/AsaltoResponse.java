package com.tp.frontend.dto.Asalto;

import com.tp.frontend.dto.Sucursal.SucursalResponse;

import java.time.LocalDate;
import java.util.List;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse; //  importar DTO de persona detenida

public class AsaltoResponse {
    private Long id;
    private String codigo;
    private LocalDate fechaAsalto;

    // RELACIÓN 1 a 1: Cada asalto ocurre en UNA sola sucursal
    private SucursalResponse sucursal;

    // Lista - RELACIÓN 1 a N: Un asalto es cometido por MUCHAS personas
    private List<PersonaDetenidaResponse> personas;




    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDate getFechaAsalto() { return fechaAsalto; }
    public void setFechaAsalto(LocalDate fechaAsalto) { this.fechaAsalto = fechaAsalto; }


    public SucursalResponse getSucursal() { return sucursal; }
    public void setSucursal(SucursalResponse sucursal) { this.sucursal = sucursal; }


    // Getter y Setter para la lista
    public List<PersonaDetenidaResponse> getPersonas() {
        return personas;
    }
    public void setPersonas(List<PersonaDetenidaResponse> personas) {
        this.personas = personas;
    }
}
