package com.tp.frontend.client;

import com.tp.frontend.config.FrontendProperties;
import com.tp.frontend.dto.contrato.ContratoRequest;
import com.tp.frontend.dto.contrato.ContratoResponse;
import com.tp.frontend.dto.contrato.ContratoUpdate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ContratosApiClient extends BaseApiClient {

    public ContratosApiClient(RestTemplate restTemplate, FrontendProperties props) {
        super(restTemplate, props);
    }

    public List<ContratoResponse> list(String jwt) {
        return get("/api/contratos", jwt, new ParameterizedTypeReference<List<ContratoResponse>>() {});
    }

    public List<ContratoResponse> listPorSucursal(String jwt, Long sucursalId) {
        return get("/api/contratos/sucursal/" + sucursalId, jwt, new ParameterizedTypeReference<List<ContratoResponse>>() {});
    }

    public List<ContratoResponse> listPorVigilante(String jwt, Long vigilanteId) {
        return get("/api/contratos/vigilante/" + vigilanteId, jwt, new ParameterizedTypeReference<List<ContratoResponse>>() {});
    }

    public ContratoResponse getById(String jwt, Long id) {
        return get("/api/contratos/" + id, jwt, ContratoResponse.class);
    }

    public ContratoResponse create(String jwt, ContratoRequest dto) {
        return post("/api/contratos", dto, jwt, ContratoResponse.class);
    }

    public ContratoResponse update(String jwt, Long id, ContratoUpdate dto) {
        return put("/api/contratos/" + id, dto, jwt, ContratoResponse.class);
    }

    public void delete(String jwt, Long id) {
        delete("/api/contratos/" + id, jwt);
    }
}
