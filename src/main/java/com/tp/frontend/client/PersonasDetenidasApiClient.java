package com.tp.frontend.client;

import com.tp.frontend.dto.PersonaDetenida.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class PersonasDetenidasApiClient extends BaseApiClient {

    public PersonasDetenidasApiClient(WebClient webClient) {
        super(webClient);
    }

    public PersonaDetenidaResponse create(String jwt, PersonaDetenidaRequest req) {
        return post("/personasDetenidas", jwt, req, PersonaDetenidaResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/personasDetenidas/" +id, jwt);
    }
    public PersonaDetenidaResponse update(String jwt, Long id, PersonaDetenidaUpdate req) {
        return put("/personasDetenidas" + id, jwt, req, PersonaDetenidaResponse.class);
    }

    public PersonaDetenidaResponse getById(String jwt, Long id) {
        return get("/personasDetenidas/" + id, jwt, PersonaDetenidaResponse.class);
    }

    public List<PersonaDetenidaResponse> list(String jwt) {
        return getList(
                "/personasDetenidas",
                jwt,
                new ParameterizedTypeReference<List<PersonaDetenidaResponse>>() {}
        );
    }
}
