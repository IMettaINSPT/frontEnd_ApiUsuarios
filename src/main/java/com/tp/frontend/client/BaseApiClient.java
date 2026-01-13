package com.tp.frontend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public abstract class BaseApiClient {

    protected final RestTemplate restTemplate;

    @Value("${backend.base-url}")
    protected String backendBaseUrl;

    protected BaseApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected HttpEntity<?> authEntity(String jwt, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    protected <T> T get(String jwt, String path, ParameterizedTypeReference<T> type) {
        ResponseEntity<T> resp = restTemplate.exchange(
                backendBaseUrl + path,
                HttpMethod.GET,
                authEntity(jwt, null),
                type
        );
        return resp.getBody();
    }

    protected <T> T post(String jwt, String path, Object body, Class<T> responseType) {
        ResponseEntity<T> resp = restTemplate.exchange(
                backendBaseUrl + path,
                HttpMethod.POST,
                authEntity(jwt, body),
                responseType
        );
        return resp.getBody();
    }

    protected <T> T put(String jwt, String path, Object body, Class<T> responseType) {
        ResponseEntity<T> resp = restTemplate.exchange(
                backendBaseUrl + path,
                HttpMethod.PUT,
                authEntity(jwt, body),
                responseType
        );
        return resp.getBody();
    }

    protected void delete(String jwt, String path) {
        restTemplate.exchange(
                backendBaseUrl + path,
                HttpMethod.DELETE,
                authEntity(jwt, null),
                Void.class
        );
    }
}
