package com.tp.frontend.client;

import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.User.UserResponse; // <--- Importar la nueva clase
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthApiClient extends BaseApiClient {

    public AuthApiClient(WebClient webClient) {
        super(webClient);
    }

    // POST /api/auth/login
    public LoginResponse login(LoginRequest dto) {
        return post("/auth/login", null, dto, LoginResponse.class);
    }

    // GET /api/usuarios/me
    // Cambiamos MeResponse por UserResponse para que coincida con el resto del sistema
    public UserResponse me(String jwt) {
        return get("/usuarios/me", jwt, UserResponse.class);
    }
}