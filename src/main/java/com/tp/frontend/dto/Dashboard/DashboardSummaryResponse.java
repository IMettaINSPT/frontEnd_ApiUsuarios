package com.tp.frontend.dto.Dashboard;

import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {

    private long bancos;
    private long sucursales;
    private long contratos;
    private long vigilantes;
    private long usuarios;

    private Map<String, Long> usuariosPorRol;
    private Map<String, Long> contratosPorArma;
    private List<MesCantidad> asaltosPorMes;

    public static class MesCantidad {
        private String mes;
        private long cantidad;

        public String getMes() { return mes; }
        public void setMes(String mes) { this.mes = mes; }

        public long getCantidad() { return cantidad; }
        public void setCantidad(long cantidad) { this.cantidad = cantidad; }
    }

    public long getBancos() { return bancos; }
    public void setBancos(long bancos) { this.bancos = bancos; }

    public long getSucursales() { return sucursales; }
    public void setSucursales(long sucursales) { this.sucursales = sucursales; }

    public long getContratos() { return contratos; }
    public void setContratos(long contratos) { this.contratos = contratos; }

    public long getVigilantes() { return vigilantes; }
    public void setVigilantes(long vigilantes) { this.vigilantes = vigilantes; }

    public long getUsuarios() { return usuarios; }
    public void setUsuarios(long usuarios) { this.usuarios = usuarios; }

    public Map<String, Long> getUsuariosPorRol() { return usuariosPorRol; }
    public void setUsuariosPorRol(Map<String, Long> usuariosPorRol) { this.usuariosPorRol = usuariosPorRol; }

    public Map<String, Long> getContratosPorArma() { return contratosPorArma; }
    public void setContratosPorArma(Map<String, Long> contratosPorArma) { this.contratosPorArma = contratosPorArma; }

    public List<MesCantidad> getAsaltosPorMes() { return asaltosPorMes; }
    public void setAsaltosPorMes(List<MesCantidad> asaltosPorMes) { this.asaltosPorMes = asaltosPorMes; }
}
