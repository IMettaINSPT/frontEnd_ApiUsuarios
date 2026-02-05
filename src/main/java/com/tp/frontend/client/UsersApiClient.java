package com.tp.frontend.client;

import com.tp.frontend.dto.User.*;
import com.tp.frontend.dto.User.UserResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class UsersApiClient extends BaseApiClient {

    public UsersApiClient(WebClient webClient) {
        super(webClient);
    }

    public UserResponse create(String jwt, UserRequest req) {
        return post("/usuarios", jwt, req, UserResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/usuarios/" +id, jwt);
    }
    public UserResponse update(String jwt, Long id, UserUpdate req) {
        return put("/usuarios/" +id, jwt, req, UserResponse.class);
    }

    public UserResponse getById(String jwt, Long id) {
        return get("/usuarios/" + id, jwt, UserResponse.class);
    }
    
    public List<UserResponse> list(String jwt) {
        return getList(
                "/usuarios",
                jwt,
                new ParameterizedTypeReference<List<UserResponse>>() {}
        );
    }
}
