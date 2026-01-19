package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Banda.BandaRequest;
import com.tp.frontend.dto.Banda.BandaResponse;
import com.tp.frontend.dto.Banda.BandaUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BandasApiClient extends BaseApiClient {

    public BandasApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<BandaResponse> list(String jwt) {
        return get("/api/bandas", jwt, new ParameterizedTypeReference<List<BandaResponse>>() {});
    }

    public BandaResponse getById(String jwt, Long id) {
        return get("/api/bandas/" + id, jwt, BandaResponse.class);
    }

    public BandaResponse create(String jwt, BandaRequest dto) {
        return post("/api/bandas", dto, jwt, BandaResponse.class);
    }

    public BandaResponse update(String jwt, Long id, BandaUpdate dto) {
        return put("/api/bandas/" + id, dto, jwt, BandaResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/bandas/" + id, jwt);
    }
}
