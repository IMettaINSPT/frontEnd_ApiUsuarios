package com.tp.frontend.dto.Juicio;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class JuicioUpdate {

        @NotBlank(message = "El número de expediente es obligatorio")
        private String expediente;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull(message = "La fecha del juicio es obligatoria")
        private LocalDate fechaJuicio;

        // CAMBIO: De String a boolean para coincidir con el resto del sistema
        @NotNull(message = "Debe indicar si la persona fue condenada o no")
        private boolean condenado;

        @NotNull(message = "El juez es obligatorio")
        private Long juezId;

        @NotNull(message = "El asalto/delito es obligatorio")
        private Long asaltoId;

        @NotNull(message = "La persona detenida es obligatoria")
        private Long personaDetenidaId;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaInicioCondena;

        @Min(value = 1, message = "El tiempo de condena debe ser al menos 1 mes")
        private Integer tiempoCondenaMeses;

        public JuicioUpdate() {}

        // Getters y Setters
        public String getExpediente() { return expediente; }
        public void setExpediente(String expediente) { this.expediente = expediente; }

        public LocalDate getFechaJuicio() { return fechaJuicio; }
        public void setFechaJuicio(LocalDate fechaJuicio) { this.fechaJuicio = fechaJuicio; }

        public boolean isCondenado() { return condenado; }
        public void setCondenado(boolean condenado) { this.condenado = condenado; }

        public Long getJuezId() { return juezId; }
        public void setJuezId(Long juezId) { this.juezId = juezId; }

        public Long getAsaltoId() { return asaltoId; }
        public void setAsaltoId(Long asaltoId) { this.asaltoId = asaltoId; }

        public Long getPersonaDetenidaId() { return personaDetenidaId; }
        public void setPersonaDetenidaId(Long personaDetenidaId) { this.personaDetenidaId = personaDetenidaId; }

        public LocalDate getFechaInicioCondena() { return fechaInicioCondena; }
        public void setFechaInicioCondena(LocalDate fechaInicioCondena) { this.fechaInicioCondena = fechaInicioCondena; }

        public Integer getTiempoCondenaMeses() { return tiempoCondenaMeses; }
        public void setTiempoCondenaMeses(Integer tiempoCondenaMeses) { this.tiempoCondenaMeses = tiempoCondenaMeses; }
}