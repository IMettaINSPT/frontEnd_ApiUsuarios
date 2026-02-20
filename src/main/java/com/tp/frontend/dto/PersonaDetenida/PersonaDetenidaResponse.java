package com.tp.frontend.dto.PersonaDetenida;

import com.tp.frontend.dto.Banda.BandaResponse;
import java.util.List;
import com.tp.frontend.dto.Asalto.AsaltoResponse;

public record PersonaDetenidaResponse(
        Long id,
        String codigo,
        String nombre,
        String apellido,
        BandaResponse banda,
        List<AsaltoResponse> asaltos
) {}
