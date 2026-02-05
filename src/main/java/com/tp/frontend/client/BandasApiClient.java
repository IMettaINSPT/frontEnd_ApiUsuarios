package com.tp.frontend.client;

import com.tp.frontend.dto.Banda.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BandasApiClient extends BaseApiClient {

    public BandasApiClient(WebClient webClient) {
        super(webClient);
    }

    public BandaResponse create(String jwt, BandaRequest req) {
        return post("/bandas", jwt, req, BandaResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/bandas" + id, jwt);
    }
    public BandaResponse update(String jwt, Long id, BandaUpdate req) {
        return put("/bandas/" +id, jwt, req, BandaResponse.class);
    }

    public BandaResponse getById(String jwt, Long id) {
        return get("/bandas/" + id, jwt, BandaResponse.class);
    }

    public List<BandaResponse> list(String jwt) {
        return getList(
                "/bandas",
                jwt,
                new ParameterizedTypeReference<List<BandaResponse>>() {}
        );
    }
}
