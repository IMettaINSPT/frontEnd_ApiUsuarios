package com.tp.frontend.service;

import com.tp.frontend.client.SucursalesApiClient;
import com.tp.frontend.dto.Sucursal.SucursalRequest;
import com.tp.frontend.dto.Sucursal.SucursalResponse;
import com.tp.frontend.dto.Sucursal.SucursalUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalService {

    private final SucursalesApiClient api;

    public SucursalService(SucursalesApiClient api) {
        this.api = api;
    }

    public List<SucursalResponse> list(String jwt) { return api.list(jwt); }
    public SucursalResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public SucursalResponse create(String jwt, SucursalRequest dto) { return api.create(jwt, dto); }
    public SucursalResponse update(String jwt, Long id, SucursalUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
