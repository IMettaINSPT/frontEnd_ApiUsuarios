package com.tp.frontend.service;

import com.tp.frontend.client.BancosApiClient;
import com.tp.frontend.dto.Banco.BancoRequest;
import com.tp.frontend.dto.Banco.BancoResponse;
import com.tp.frontend.dto.Banco.BancoUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BancoService {

    private final BancosApiClient api;

    public BancoService(BancosApiClient api) {
        this.api = api;
    }

    public List<BancoResponse> list(String jwt) { return api.list(jwt); }
    public BancoResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public BancoResponse create(String jwt, BancoRequest dto) { return api.create(jwt, dto); }
    public BancoResponse update(String jwt, Long id, BancoUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
