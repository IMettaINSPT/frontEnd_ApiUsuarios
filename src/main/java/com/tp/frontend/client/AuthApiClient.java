package com.tp.frontend.client;

import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.Login.MeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthApiClient {

    private final RestTemplate restTemplate;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    public AuthApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginResponse login(LoginRequest dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(dto, headers);

        return restTemplate.postForObject(
                backendBaseUrl + "/api/auth/login",
                entity,
                LoginResponse.class
        );
    }

    public MeResponse me(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<MeResponse> resp = restTemplate.exchange(
                backendBaseUrl + "/api/auth/me",
                HttpMethod.GET,
                entity,
                MeResponse.class
        );
        return resp.getBody();
    }
}
