package com.tp.frontend.client;

import com.tp.frontend.dto.Juicio.JuicioRequest;
import com.tp.frontend.dto.Juicio.JuicioResponse;
import com.tp.frontend.dto.Juicio.JuicioUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class JuiciosApiClient extends BaseApiClient {

    public JuiciosApiClient(WebClient webClient) {
        super(webClient);
    }

    public JuicioResponse create(String jwt, JuicioRequest req) {
        return post("/juicios", jwt, req, JuicioResponse.class);
    }

    public void delete(String jwt, Long id) {
         delete("/juicios/" +id, jwt);
    }
    public JuicioResponse update(String jwt, Long id, JuicioUpdate req) {
        return put("/juicios//" +id, jwt, req, JuicioResponse.class);
    }

    public JuicioResponse getById(String jwt, Long id) {
        return get("/juicios/" + id, jwt, JuicioResponse.class);
    }


    public List<JuicioResponse> list(String jwt) {
        return getList(
                "/juicios",
                jwt,
                new ParameterizedTypeReference<List<JuicioResponse>>() {}
        );
    }
}
