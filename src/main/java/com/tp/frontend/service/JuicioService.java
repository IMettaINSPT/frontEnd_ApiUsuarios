package com.tp.frontend.service;

import com.tp.frontend.client.JuiciosApiClient;
import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioResponse;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JuicioService {

    private final JuiciosApiClient api;

    public JuicioService(JuiciosApiClient api) {
        this.api = api;
    }

    public List<JuicioResponse> list(String jwt) { return api.list(jwt); }
    public JuicioResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public JuicioResponse create(String jwt, JuicioRequest dto) { return api.create(jwt, dto); }
    public JuicioResponse update(String jwt, Long id, JuicioUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
