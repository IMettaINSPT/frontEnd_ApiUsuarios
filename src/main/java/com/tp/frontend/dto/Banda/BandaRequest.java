package com.tp.frontend.dto.Banda;

import jakarta.validation.constraints.NotNull;

public class BandaRequest {
    @NotNull(message="El numero de banda es obligatorio")
    private Integer numeroBanda;

    @NotNull(message="El numero de miembros es obligatorio")
    private Integer numeroMiembros;

    public Integer getNumeroBanda() { return numeroBanda; }
    public void setNumeroBanda(Integer numeroBanda) { this.numeroBanda = numeroBanda; }

    public Integer getNumeroMiembros() { return numeroMiembros; }
    public void setNumeroMiembros(Integer numeroMiembros) { this.numeroMiembros = numeroMiembros; }
}
