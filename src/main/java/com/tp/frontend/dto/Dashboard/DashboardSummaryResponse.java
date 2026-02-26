package com.tp.frontend.dto.Dashboard;

import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {

    private long bancos;
    private long sucursales;
    private long contratos;
    private long vigilantes;
    private long usuarios;
    // --- ESTO FALTABA EN EL FRONTEND ---
    private long jueces;
    private long juicios;
    private long bandas;
    private long detenidos;
    private long asaltos;

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

    // Getters y Setters de los campos base
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

    // --- NUEVOS GETTERS Y SETTERS EN FRONTEND ---
    public long getJueces() { return jueces; }
    public void setJueces(long jueces) { this.jueces = jueces; }
    public long getJuicios() { return juicios; }
    public void setJuicios(long juicios) { this.juicios = juicios; }
    public long getBandas() { return bandas; }
    public void setBandas(long bandas) { this.bandas = bandas; }
    public long getDetenidos() { return detenidos; }
    public void setDetenidos(long detenidos) { this.detenidos = detenidos; }
    public long getAsaltos() { return asaltos; }
    public void setAsaltos(long asaltos) { this.asaltos = asaltos; }

    public Map<String, Long> getUsuariosPorRol() { return usuariosPorRol; }
    public void setUsuariosPorRol(Map<String, Long> usuariosPorRol) { this.usuariosPorRol = usuariosPorRol; }
    public Map<String, Long> getContratosPorArma() { return contratosPorArma; }
    public void setContratosPorArma(Map<String, Long> contratosPorArma) { this.contratosPorArma = contratosPorArma; }
    public List<MesCantidad> getAsaltosPorMes() { return asaltosPorMes; }
    public void setAsaltosPorMes(List<MesCantidad> asaltosPorMes) { this.asaltosPorMes = asaltosPorMes; }
}