package com.tp.frontend.client;

import com.tp.frontend.dto.Vigilante.VigilanteResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class VigilantesApiClient extends BaseApiClient {

    public VigilantesApiClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public List<VigilanteResponse> disponibles(String jwt) {
        return get(jwt, "/api/vigilantes/disponibles",
                new ParameterizedTypeReference<List<VigilanteResponse>>() {});
    }
}
