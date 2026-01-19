package com.tp.frontend.dto.Banco;

public class BancoRequest {
    private String codigo;
    private String domicilioCentral;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDomicilioCentral() { return domicilioCentral; }
    public void setDomicilioCentral(String domicilioCentral) { this.domicilioCentral = domicilioCentral; }
}
