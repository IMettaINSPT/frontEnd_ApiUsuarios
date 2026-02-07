package com.tp.frontend.client;

import com.tp.frontend.dto.Banco.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BancosApiClient extends BaseApiClient {

    public BancosApiClient(WebClient webClient) {
        super(webClient);
    }

    public BancoResponse create(String jwt, BancoRequest req) {
        return post("/bancos", jwt, req, BancoResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/bancos/" + id, jwt);
    }
    public BancoResponse update(String jwt, Long id, BancoUpdate req) {
        return put("/bancos/"+ id, jwt, req, BancoResponse.class);
    }

    public BancoResponse getById(String jwt, Long id) {
        return get("/bancos/" + id, jwt, BancoResponse.class);
    }

    public List<BancoResponse> list(String jwt) {
        return getList(
                "/bancos",
                jwt,
                new ParameterizedTypeReference<List<BancoResponse>>() {}
        );
    }
}
