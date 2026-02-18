package com.tp.frontend.dto.Banda;
import java.util.List;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse; //  importar DTO de persona detenida


public class  BandaResponse {
         Long id;
         Integer numeroBanda;
         Integer numeroMiembros;

        // Lista
        private List<PersonaDetenidaResponse> personasDetenidas;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Integer getNumeroBanda() { return numeroBanda; }
        public void setNumeroBanda(Integer numeroBanda) { this.numeroBanda = numeroBanda; }

        public Integer getNumeroMiembros() { return numeroMiembros; }
        public void setNumeroMiembros(Integer numeroMiembros) { this.numeroMiembros = numeroMiembros; }


        // Getter y Setter para la lista
        public List<PersonaDetenidaResponse> getPersonasDetenidas() {
            return personasDetenidas;
        }
        public void setPersonasDetenidas(List<PersonaDetenidaResponse> personasDetenidas) {
            this.personasDetenidas = personasDetenidas;
        }


}



