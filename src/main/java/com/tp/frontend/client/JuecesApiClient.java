package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Juez.JuezRequest;
import com.tp.frontend.dto.Juez.JuezResponse;
import com.tp.frontend.dto.Juez.JuezUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class JuecesApiClient extends BaseApiClient {

    public JuecesApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<JuezResponse> list(String jwt) {
        return get("/api/jueces", jwt, new ParameterizedTypeReference<List<JuezResponse>>() {});
    }

    public JuezResponse getById(String jwt, Long id) {
        return get("/api/jueces/" + id, jwt, JuezResponse.class);
    }

    public JuezResponse create(String jwt, JuezRequest dto) {
        return post("/api/jueces", dto, jwt, JuezResponse.class);
    }

    public JuezResponse update(String jwt, Long id, JuezUpdate dto) {
        return put("/api/jueces/" + id, dto, jwt, JuezResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/jueces/" + id, jwt);
    }
}
