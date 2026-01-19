package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.Asalto.AsaltoRequest;
import com.tp.frontend.dto.Asalto.AsaltoResponse;
import com.tp.frontend.dto.Asalto.AsaltoUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AsaltosApiClient extends BaseApiClient {

    public AsaltosApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<AsaltoResponse> list(String jwt) {
        return get("/api/asaltos", jwt, new ParameterizedTypeReference<List<AsaltoResponse>>() {});
    }

    public List<AsaltoResponse> search(String jwt,
                                       Long sucursalId,
                                       LocalDate fecha,
                                       LocalDate desde,
                                       LocalDate hasta) {

        List<String> params = new ArrayList<>();

        if (sucursalId != null) params.add("sucursalId=" + sucursalId);
        if (fecha != null) params.add("fecha=" + enc(fecha.toString()));
        if (desde != null) params.add("desde=" + enc(desde.toString()));
        if (hasta != null) params.add("hasta=" + enc(hasta.toString()));

        String path = "/api/asaltos" + (params.isEmpty() ? "" : "?" + String.join("&", params));

        return get(path, jwt, new ParameterizedTypeReference<List<AsaltoResponse>>() {});
    }

    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    public AsaltoResponse getById(String jwt, Long id) {
        return get("/api/asaltos/" + id, jwt, AsaltoResponse.class);
    }

    public AsaltoResponse create(String jwt, AsaltoRequest dto) {
        return post("/api/asaltos", dto, jwt, AsaltoResponse.class);
    }

    public AsaltoResponse update(String jwt, Long id, AsaltoUpdate dto) {
        return put("/api/asaltos/" + id, dto, jwt, AsaltoResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/asaltos/" + id, jwt);
    }
}
