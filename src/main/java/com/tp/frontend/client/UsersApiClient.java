package com.tp.frontend.client;

import com.tp.frontend.dto.User.UserResponse;         // tu "response"
import com.tp.frontend.dto.User.UserRequest;   // tu "request" de alta
import com.tp.frontend.dto.User.UserUpdate;   // crear este DTO en front (ver abajo)
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class UsersApiClient extends BaseApiClient {

    public UsersApiClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    // GET /api/usuarios
    public List<UserResponse> list(String jwt) {
        return get(jwt, "/api/usuarios", new ParameterizedTypeReference<List<UserResponse>>() {});
    }

    // GET /api/usuarios/{id}
    public UserResponse getById(String jwt, Long id) {
        return get(jwt, "/api/usuarios/" + id, new ParameterizedTypeReference<UserResponse>() {});
    }

    // POST /api/usuarios  (UsuarioRequest)
    public void create(String jwt, UserRequest dto) {
        post(jwt, "/api/usuarios", dto, UserResponse.class);
    }

    // PUT /api/usuarios/{id} (UsuarioUpdateRequest)
    public void update(String jwt, Long id, UserUpdate dto) {
        put(jwt, "/api/usuarios/" + id, dto, UserResponse.class);
    }

    // DELETE /api/usuarios/{id}
    public void delete(String jwt, Long id) {
        delete(jwt, "/api/usuarios/" + id);
    }
}
