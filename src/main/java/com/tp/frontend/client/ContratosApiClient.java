package com.tp.frontend.client;

import com.tp.frontend.dto.contrato.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ContratosApiClient extends BaseApiClient {

    public ContratosApiClient(WebClient webClient) {
        super(webClient);
    }

    public ContratoResponse create(String jwt, ContratoRequest req) {
        return post("/contratos", jwt, req, ContratoResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/contratos/" +id, jwt);
    }
    public ContratoResponse update(String jwt, Long id, ContratoUpdate req) {
        return put("/contratos/" +id, jwt, req, ContratoResponse.class);
    }

    public ContratoResponse getById(String jwt, Long id) {
        return get("/contratos/" + id, jwt, ContratoResponse.class);
    }

    public List<ContratoResponse> list(String jwt) {
        return getList(
                "/contratos",
                jwt,
                new ParameterizedTypeReference<List<ContratoResponse>>() {}
        );
    }
    public List<ContratoResponse> listPorSucursal(String jwt,Long sucursalId) {
        return getList(
                "/contratos/sucursal/" + sucursalId,
                jwt,
                new ParameterizedTypeReference<List<ContratoResponse>>() {}
        );
    }
    public List<ContratoResponse> listPorVigilante(String jwt, Long vigilanteId) {
        return getList(
                "/contratos/vigilante" + vigilanteId,
                jwt,
                new ParameterizedTypeReference<List<ContratoResponse>>() {}
        );
    }
}
