package com.tp.frontend.client;

import com.tp.frontend.dto.Login.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthApiClient extends BaseApiClient {

    public AuthApiClient(WebClient webClient) {
        super(webClient);
    }

    // POST /api/auth/login
    // NO lleva JWT (todavía no existe)
    public LoginResponse login(LoginRequest dto) {
        return post("/auth/login", null, dto, LoginResponse.class);
    }

    // GET /api/auth/me
    // SÍ lleva JWT
    public MeResponse me(String jwt) {
        return get("/auth/me", jwt, MeResponse.class);
    }
}