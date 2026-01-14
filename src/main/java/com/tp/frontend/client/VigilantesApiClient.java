package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Vigilante.VigilanteRequest;
import com.tp.frontend.dto.Vigilante.VigilanteResponse;
import com.tp.frontend.dto.Vigilante.VigilanteUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class VigilantesApiClient extends BaseApiClient {

    public VigilantesApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    // GET /api/vigilantes
    public List<VigilanteResponse> list(String jwt) {
        return get("/api/vigilantes", jwt, new ParameterizedTypeReference<List<VigilanteResponse>>() {});
    }

    // GET /api/vigilantes/{id}
    public VigilanteResponse getById(String jwt, Long id) {
        return get("/api/vigilantes/" + id, jwt, VigilanteResponse.class);
    }

    // POST /api/vigilantes
    public VigilanteResponse create(String jwt, VigilanteRequest dto) {
        return post("/api/vigilantes", dto, jwt, VigilanteResponse.class);
    }

    // PUT /api/vigilantes/{id}
    public VigilanteResponse update(String jwt, Long id, VigilanteUpdate dto) {
        return put("/api/vigilantes/" + id, dto, jwt, VigilanteResponse.class);
    }

    // DELETE /api/vigilantes/{id}
    public void delete(String jwt, Long id) {
        delete("/api/vigilantes/" + id, jwt);
    }

    // GET /api/vigilantes/disponibles
    public List<VigilanteResponse> disponibles(String jwt) {
        return get("/api/vigilantes/disponibles", jwt, new ParameterizedTypeReference<List<VigilanteResponse>>() {});
    }

    // GET /api/vigilantes/me
    public VigilanteResponse me(String jwt) {
        return get("/api/vigilantes/me", jwt, VigilanteResponse.class);
    }
}
