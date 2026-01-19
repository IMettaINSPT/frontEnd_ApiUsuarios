package com.tp.frontend.service;

import com.tp.frontend.client.ContratosApiClient;
import com.tp.frontend.dto.contrato.ContratoRequest;
import com.tp.frontend.dto.contrato.ContratoResponse;
import com.tp.frontend.dto.contrato.ContratoUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratoService {

    private final ContratosApiClient api;

    public ContratoService(ContratosApiClient api) {
        this.api = api;
    }

    public List<ContratoResponse> list(String jwt) { return api.list(jwt); }
    public List<ContratoResponse> listPorSucursal(String jwt, Long sucursalId) { return api.listPorSucursal(jwt, sucursalId); }
    public List<ContratoResponse> listPorVigilante(String jwt, Long vigilanteId) { return api.listPorVigilante(jwt, vigilanteId); }

    public ContratoResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public ContratoResponse create(String jwt, ContratoRequest dto) { return api.create(jwt, dto); }
    public ContratoResponse update(String jwt, Long id, ContratoUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
