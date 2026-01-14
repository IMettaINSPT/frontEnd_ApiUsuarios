package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.Login.MeResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthApiClient extends BaseApiClient {

    public AuthApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    // POST /api/auth/login
    public LoginResponse login(LoginRequest dto) {
        // login NO lleva JWT
        return post("/api/auth/login", dto, null, LoginResponse.class);
    }

    // GET /api/auth/me
    public MeResponse me(String jwt) {
        return get("/api/auth/me", jwt, MeResponse.class);
    }
}
