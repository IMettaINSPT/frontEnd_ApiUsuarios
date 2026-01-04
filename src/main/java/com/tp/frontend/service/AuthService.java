package com.tp.frontend.service;

import com.tp.frontend.client.AuthApiClient;
import com.tp.frontend.dto.LoginRequestDTO;
import com.tp.frontend.dto.LoginResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    public String loginAndGetToken(String username, String password) {

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername(username);
        request.setPassword(password);

        LoginResponseDTO response = authApiClient.login(request);

        if (response == null || response.getAccessToken() == null) {
            return null;
        }

        return response.getAccessToken();
    }
}
