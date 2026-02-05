package com.tp.frontend.client;

import com.tp.frontend.dto.Juez.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class JuecesApiClient extends BaseApiClient {

    public JuecesApiClient(WebClient webClient) {
        super(webClient);
    }

    public JuezResponse create(String jwt, JuezRequest req) {
        return post("/jueces", jwt, req, JuezResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/jueces/" +id, jwt);
    }
    public JuezResponse update(String jwt, Long id, JuezUpdate req) {
        return put("/jueces/" +id, jwt, req, JuezResponse.class);
    }

    public JuezResponse getById(String jwt, Long id) {
        return get("/jueces/" + id, jwt, JuezResponse.class);
    }

    public List<JuezResponse> list(String jwt) {
        return getList(
                "/jueces",
                jwt,
                new ParameterizedTypeReference<List<JuezResponse>>() {}
        );
    }
}
