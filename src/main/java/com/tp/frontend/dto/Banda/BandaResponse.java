package com.tp.frontend.dto.Banda;

public class BandaResponse {
    private Long id;
    private Integer numeroBanda;
    private Integer numeroMiembros;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumeroBanda() { return numeroBanda; }
    public void setNumeroBanda(Integer numeroBanda) { this.numeroBanda = numeroBanda; }

    public Integer getNumeroMiembros() { return numeroMiembros; }
    public void setNumeroMiembros(Integer numeroMiembros) { this.numeroMiembros = numeroMiembros; }
}
