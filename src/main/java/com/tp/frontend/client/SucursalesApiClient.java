package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Sucursal.SucursalRequest;
import com.tp.frontend.dto.Sucursal.SucursalResponse;
import com.tp.frontend.dto.Sucursal.SucursalUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class SucursalesApiClient extends BaseApiClient {

    public SucursalesApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<SucursalResponse> list(String jwt) {
        return get("/api/sucursales", jwt, new ParameterizedTypeReference<List<SucursalResponse>>() {});
    }

    public SucursalResponse getById(String jwt, Long id) {
        return get("/api/sucursales/" + id, jwt, SucursalResponse.class);
    }

    public SucursalResponse create(String jwt, SucursalRequest dto) {
        return post("/api/sucursales", dto, jwt, SucursalResponse.class);
    }

    public SucursalResponse update(String jwt, Long id, SucursalUpdate dto) {
        return put("/api/sucursales/" + id, dto, jwt, SucursalResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/sucursales/" + id, jwt);
    }
}
