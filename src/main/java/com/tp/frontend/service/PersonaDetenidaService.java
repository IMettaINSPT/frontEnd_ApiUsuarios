package com.tp.frontend.service;

import com.tp.frontend.client.PersonasDetenidasApiClient;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaRequest;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaResponse;
import com.tp.frontend.dto.PersonaDetenida.PersonaDetenidaUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaDetenidaService {

    private final PersonasDetenidasApiClient api;

    public PersonaDetenidaService(PersonasDetenidasApiClient api) {
        this.api = api;
    }

    public List<PersonaDetenidaResponse> list(String jwt) { return api.list(jwt); }
    public PersonaDetenidaResponse get(String jwt, Long id) { return api.getById(jwt, id); }
    public PersonaDetenidaResponse create(String jwt, PersonaDetenidaRequest dto) { return api.create(jwt, dto); }
    public PersonaDetenidaResponse update(String jwt, Long id, PersonaDetenidaUpdate dto) { return api.update(jwt, id, dto); }
    public void delete(String jwt, Long id) { api.delete(jwt, id); }
}
