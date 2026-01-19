package com.tp.frontend.service;

import com.tp.frontend.client.BandasApiClient;
import com.tp.frontend.dto.Banda.BandaRequest;
import com.tp.frontend.dto.Banda.BandaResponse;
import com.tp.frontend.dto.Banda.BandaUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BandaService {

    private final BandasApiClient api;

    public BandaService(BandasApiClient api) {
        this.api = api;
    }

    public List<BandaResponse> list(String jwt) { return api.list(jwt); }
    public BandaResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public BandaResponse create(String jwt, BandaRequest dto) { return api.create(jwt, dto); }
    public BandaResponse update(String jwt, Long id, BandaUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
