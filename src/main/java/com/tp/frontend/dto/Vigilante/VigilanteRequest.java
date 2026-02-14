package com.tp.frontend.dto.Vigilante;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VigilanteRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "La edad mínima es 18")
    private Integer edad;

    @NotNull(message="El Nro de Contrato es obligatorio")
    private Long contratoId;

    public String getCodigo() { return codigo; }
    public Integer getEdad() { return edad; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }

}
