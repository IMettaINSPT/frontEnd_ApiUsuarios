package com.tp.frontend.service;

import com.tp.frontend.client.JuecesApiClient;
import com.tp.frontend.dto.Juez.JuezRequest;
import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.Juez.JuezUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JuezService {

    private final JuecesApiClient api;

    public JuezService(JuecesApiClient api) {
        this.api = api;
    }

    public List<JuezResponse> list(String jwt) { return api.list(jwt); }
    public JuezResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public JuezResponse create(String jwt, JuezRequest dto) { return api.create(jwt, dto); }
    public JuezResponse update(String jwt, Long id, JuezUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
