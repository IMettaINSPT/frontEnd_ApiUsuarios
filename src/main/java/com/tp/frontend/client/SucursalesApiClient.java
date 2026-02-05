package com.tp.frontend.client;

import com.tp.frontend.dto.Sucursal.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SucursalesApiClient extends BaseApiClient {

    public SucursalesApiClient(WebClient webClient) {
        super(webClient);
    }

    public SucursalResponse create(String jwt, SucursalRequest req) {
        return post("/sucursales", jwt, req, SucursalResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/sucursales/" +id, jwt);
    }
    public SucursalResponse update(String jwt, Long id, SucursalUpdate req) {
        return put("/sucursales/" +id, jwt, req, SucursalResponse.class);
    }

    public SucursalResponse getById(String jwt, Long id) {
        return get("/sucursales/" + id, jwt, SucursalResponse.class);
    }

    public List<SucursalResponse> list(String jwt) {
        return getList(
                "/sucursales",
                jwt,
                new ParameterizedTypeReference<List<SucursalResponse>>() {}
        );
    }
}
