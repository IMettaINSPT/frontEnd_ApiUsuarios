package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Banco.BancoRequest;
import com.tp.frontend.dto.Banco.BancoResponse;
import com.tp.frontend.dto.Banco.BancoUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BancosApiClient extends BaseApiClient {

    public BancosApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<BancoResponse> list(String jwt) {
        return get("/api/bancos", jwt, new ParameterizedTypeReference<List<BancoResponse>>() {});
    }

    public BancoResponse getById(String jwt, Long id) {
        return get("/api/bancos/" + id, jwt, BancoResponse.class);
    }

    public BancoResponse create(String jwt, BancoRequest dto) {
        return post("/api/bancos", dto, jwt, BancoResponse.class);
    }

    public BancoResponse update(String jwt, Long id, BancoUpdate dto) {
        return put("/api/bancos/" + id, dto, jwt, BancoResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/bancos/" + id, jwt);
    }
}
