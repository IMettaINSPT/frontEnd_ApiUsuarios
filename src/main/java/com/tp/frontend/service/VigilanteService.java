package com.tp.frontend.service;

import com.tp.frontend.client.VigilantesApiClient;
import com.tp.frontend.dto.Vigilante.VigilanteRequest;
import com.tp.frontend.dto.Vigilante.VigilanteResponse;
import com.tp.frontend.dto.Vigilante.VigilanteUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VigilanteService {

    private final VigilantesApiClient api;

    public VigilanteService(VigilantesApiClient api) {
        this.api = api;
    }

    public List<VigilanteResponse> list(String jwt) { return api.list(jwt); }
    public VigilanteResponse getById(String jwt, Long id) { return api.getById(jwt, id); }
    public VigilanteResponse create(String jwt, VigilanteRequest dto) { return api.create(jwt, dto); }
    public VigilanteResponse update(String jwt, Long id, VigilanteUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
    public List<VigilanteResponse> disponibles(String jwt) { return api.disponibles(jwt); }
    public VigilanteResponse me(String jwt) { return api.me(jwt); }
}
