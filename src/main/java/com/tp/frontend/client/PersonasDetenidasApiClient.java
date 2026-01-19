package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaRequest;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PersonasDetenidasApiClient extends BaseApiClient {

    public PersonasDetenidasApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<PersonaDetenidaResponse> list(String jwt) {
        return get("/api/personasDetenidas", jwt, new ParameterizedTypeReference<List<PersonaDetenidaResponse>>() {});
    }

    public PersonaDetenidaResponse getById(String jwt, Long id) {
        return get("/api/personasDetenidas/" + id, jwt, PersonaDetenidaResponse.class);
    }

    public PersonaDetenidaResponse create(String jwt, PersonaDetenidaRequest dto) {
        return post("/api/personasDetenidas", dto, jwt, PersonaDetenidaResponse.class);
    }

    public PersonaDetenidaResponse update(String jwt, Long id, PersonaDetenidaUpdate dto) {
        return put("/api/personasDetenidas/" + id, dto, jwt, PersonaDetenidaResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/personasDetenidas/" + id, jwt);
    }
}
