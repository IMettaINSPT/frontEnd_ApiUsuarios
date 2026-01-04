package com.tp.frontend.client;

import com.tp.frontend.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class UsersApiClient {

    private final RestTemplate restTemplate;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    public UsersApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<UserDTO> list(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDTO[]> response = restTemplate.exchange(
                backendBaseUrl + "/api/usuarios",
                HttpMethod.GET,
                entity,
                UserDTO[].class
        );

        return Arrays.asList(response.getBody());
    }

    public void delete(Long id, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    backendBaseUrl + "/api/usuarios/" + id,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        } catch (HttpStatusCodeException ex) {
            throw ex; // lo maneja el controller
        }
    }
}
