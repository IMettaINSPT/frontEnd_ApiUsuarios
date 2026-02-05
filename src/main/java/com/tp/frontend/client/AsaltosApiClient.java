package com.tp.frontend.client;

import com.tp.frontend.dto.Asalto.*;
import com.tp.frontend.dto.Error.ApiError;
import com.tp.frontend.exception.ApiErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Component
public class AsaltosApiClient extends BaseApiClient {

    public AsaltosApiClient(WebClient webClient) {
        super(webClient);
    }

    public AsaltoResponse create(String jwt, AsaltoRequest req) {
        return post("/asaltos", jwt, req, AsaltoResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/asaltos" + id, jwt);
    }
    public AsaltoResponse update(String jwt, Long id, AsaltoUpdate req) {
        return put("/asaltos/" + id, jwt, req, AsaltoResponse.class);
    }

    public AsaltoResponse getById(String jwt, Long id) {
        return get("/asaltos/" + id, jwt, AsaltoResponse.class);
    }

    public List<AsaltoResponse> search(
            String jwt,
            Long sucursalId,
            LocalDate fecha,
            LocalDate desde,
            LocalDate hasta
    ) {
        return webClient.get()
                .uri(uriBuilder -> {
                    var ub = uriBuilder.path("/asaltos");
                    if (sucursalId != null) {
                        ub.queryParam("sucursalId", sucursalId);
                    }
                    if (fecha != null) {
                        ub.queryParam("fecha", fecha);
                    }
                    if (desde != null) {
                        ub.queryParam("desde", desde);
                    }
                    if (hasta != null) {
                        ub.queryParam("hasta", hasta);
                    }

                    return ub.build();
                })
                .headers(h -> applyAuth(h, jwt))
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(ApiError.class)
                                .defaultIfEmpty(new ApiError())
                                .flatMap(err -> Mono.error(
                                        new ApiErrorException(err, resp.statusCode().value())
                                ))
                )
                .bodyToMono(new ParameterizedTypeReference<List<AsaltoResponse>>() {})
                .block();
    }


    public List<AsaltoResponse> list(String jwt) {
        return getList(
                "/asaltos",
                jwt,
                new ParameterizedTypeReference<List<AsaltoResponse>>() {}
        );
    }
}
