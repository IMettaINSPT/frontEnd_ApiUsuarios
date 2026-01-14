package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.User.UserRequest;
import com.tp.frontend.dto.User.UserResponse;
import com.tp.frontend.dto.User.UserUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class UsersApiClient extends BaseApiClient {

    public UsersApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<UserResponse> list(String jwt) {
        return get("/api/usuarios", jwt,
                new ParameterizedTypeReference<List<UserResponse>>() {});
    }

    public UserResponse getById(String jwt, Long id) {
        return get("/api/usuarios/" + id, jwt, UserResponse.class);
    }

    public UserResponse create(String jwt, UserRequest dto) {
        return post("/api/usuarios", dto, jwt, UserResponse.class);
    }

    public UserResponse update(String jwt, Long id, UserUpdate dto) {
        return put("/api/usuarios/" + id, dto, jwt, UserResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/usuarios/" + id, jwt);
    }
}
