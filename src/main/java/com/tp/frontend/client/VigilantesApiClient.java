package com.tp.frontend.client;

import com.tp.frontend.dto.Vigilante.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class VigilantesApiClient extends BaseApiClient {

    public VigilantesApiClient(WebClient webClient) {
        super(webClient);
    }

    public VigilanteResponse create(String jwt, VigilanteRequest req) {
        return post("/vigilantes", jwt, req, VigilanteResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/vigilantes/" + id, jwt);
    }
    public VigilanteResponse update(String jwt, Long id, VigilanteUpdate req) {
        return put("/vigilantes/"+ id, jwt, req, VigilanteResponse.class);
    }

    public VigilanteResponse getById(String jwt, Long id) {
        return get("/vigilantes/" + id, jwt, VigilanteResponse.class);
    }

    public VigilanteResponse getMeProfile(String jwt) {
        return get("/vigilantes/me" , jwt, VigilanteResponse.class);
    }

    public List<VigilanteResponse> list(String jwt) {
        return getList(
                "/vigilantes/listar",
                jwt,
                new ParameterizedTypeReference<List<VigilanteResponse>>() {}
        );
    }
    public List<VigilanteResponse> available(String jwt) {
        return getList(
                "/vigilantes/disponibles",
                jwt,
                new ParameterizedTypeReference<List<VigilanteResponse>>() {}
        );
    }
    public Long availableCount(String jwt) {
        return get(
                "/vigilantes/disponiblesCount",
                jwt, Long.class
        );
    }

}
