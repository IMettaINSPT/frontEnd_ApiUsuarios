package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public abstract class BaseApiClient {

    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    protected BaseApiClient(RestTemplate restTemplate, FrontendProperties props) {
        this.restTemplate = restTemplate;
        this.baseUrl = props.getBackendBaseUrl();
    }

    protected <T> T get(String path, String jwt, Class<T> clazz) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(jwt));
        return restTemplate.exchange(baseUrl + path, HttpMethod.GET, entity, clazz).getBody();
    }

    protected <T> T get(String path, String jwt, ParameterizedTypeReference<T> typeRef) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(jwt));
        return restTemplate.exchange(baseUrl + path, HttpMethod.GET, entity, typeRef).getBody();
    }

    protected <T> T post(String path, Object body, String jwt, Class<T> clazz) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers(jwt));
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, entity, clazz).getBody();
    }

    protected <T> T put(String path, Object body, String jwt, Class<T> clazz) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers(jwt));
        return restTemplate.exchange(baseUrl + path, HttpMethod.PUT, entity, clazz).getBody();
    }

    protected void delete(String path, String jwt) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(jwt));
        restTemplate.exchange(baseUrl + path, HttpMethod.DELETE, entity, Void.class);
    }

    private HttpHeaders headers(String jwt) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        if (jwt != null && !jwt.isBlank()) {
            h.setBearerAuth(jwt);
        }
        return h;
    }
}
