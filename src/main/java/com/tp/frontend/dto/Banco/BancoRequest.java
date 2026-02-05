package com.tp.frontend.dto.Banco;

import jakarta.validation.constraints.NotBlank;

public class BancoRequest {

    @NotBlank(message="El codigo es obligatorio")
    private String codigo;

    @NotBlank(message="El domicilio es obligatorio")
    private String domicilioCentral;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDomicilioCentral() { return domicilioCentral; }
    public void setDomicilioCentral(String domicilioCentral) { this.domicilioCentral = domicilioCentral; }
}
