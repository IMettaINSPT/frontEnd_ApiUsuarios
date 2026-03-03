package com.tp.frontend.service;

import com.tp.frontend.client.AuthApiClient;
import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.Login.MeResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    public LoginResponse login(String username, String password) {
        return authApiClient.login(new LoginRequest(username, password));
    }

    public LoginResponse login(LoginRequest req) {
        return login(req.getUsername(), req.getPassword());
    }
    public MeResponse me(String jwt) {
        return authApiClient.me(jwt);
    }
}
