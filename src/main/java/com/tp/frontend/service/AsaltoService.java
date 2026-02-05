package com.tp.frontend.service;

import com.tp.frontend.client.AsaltosApiClient;
import com.tp.frontend.dto.Asalto.AsaltoRequest;
import com.tp.frontend.dto.Asalto.AsaltoResponse;
import com.tp.frontend.dto.Asalto.AsaltoUpdate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsaltoService {

    private final AsaltosApiClient api;

    public AsaltoService(AsaltosApiClient api) {
        this.api = api;
    }

    public List<AsaltoResponse> list(String jwt) { return api.list(jwt); }

    public List<AsaltoResponse> reporte(String jwt, Long sucursalId, LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return api.search(jwt, sucursalId, fecha, desde, hasta);
    }

    public AsaltoResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public AsaltoResponse create(String jwt, AsaltoRequest dto) { return api.create(jwt, dto); }
    public AsaltoResponse update(String jwt, Long id, AsaltoUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
