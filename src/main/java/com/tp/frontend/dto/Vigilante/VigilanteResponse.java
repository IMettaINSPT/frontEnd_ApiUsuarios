package com.tp.frontend.dto.Vigilante;
import com.tp.frontend.dto.contrato.ContratoResponse;
import java.util.List;

public class VigilanteResponse {
    private Long id;
    private String codigo;
    private Integer edad;

    // Se agrega la lista para guardar las sucursales
    private List<ContratoResponse> contratos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    // Métodos fundamentales para que el Service no falle
    public List<ContratoResponse> getContratos() { return contratos; }
    public void setContratos(List<ContratoResponse> contratos) { this.contratos = contratos; }


}
