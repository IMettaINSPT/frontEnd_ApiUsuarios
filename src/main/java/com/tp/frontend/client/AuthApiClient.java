package com.tp.frontend.client;

import com.tp.frontend.dto.LoginRequestDTO;
import com.tp.frontend.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthApiClient {

    private final RestTemplate restTemplate;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    public AuthApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        String url = backendBaseUrl + "/api/auth/login";
        System.out.println(">>> POST " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<LoginResponseDTO> response =
                    restTemplate.postForEntity(url, entity, LoginResponseDTO.class);

            System.out.println("<<< Status: " + response.getStatusCode());
            return response.getBody();

        } catch (HttpStatusCodeException ex) {
            // backend respondió 4xx/5xx
            System.out.println("<<< Status ERROR: " + ex.getStatusCode());
            System.out.println("<<< Body ERROR: " + ex.getResponseBodyAsString());
            throw ex;

        } catch (Exception ex) {
        // no pudo conectar (IP/puerto/timeout)
        System.out.println("<<< Error generico: " + ex.getMessage());
        throw ex;
    }
    }
}
