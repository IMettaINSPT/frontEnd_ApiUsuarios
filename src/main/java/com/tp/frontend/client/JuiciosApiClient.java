package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioResponse;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class JuiciosApiClient extends BaseApiClient {

    public JuiciosApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<JuicioResponse> list(String jwt) {
        return get("/api/juicios", jwt, new ParameterizedTypeReference<List<JuicioResponse>>() {});
    }

    public JuicioResponse getById(String jwt, Long id) {
        return get("/api/juicios/" + id, jwt, JuicioResponse.class);
    }

    public JuicioResponse create(String jwt, JuicioRequest dto) {
        return post("/api/juicios", dto, jwt, JuicioResponse.class);
    }

    public JuicioResponse update(String jwt, Long id, JuicioUpdate dto) {
        return put("/api/juicios/" + id, dto, jwt, JuicioResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/juicios/" + id, jwt);
    }
}
