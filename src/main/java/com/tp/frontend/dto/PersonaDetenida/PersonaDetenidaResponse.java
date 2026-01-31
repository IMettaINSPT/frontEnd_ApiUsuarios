package com.tp.frontend.dto.PersonaDetenida;

import com.tp.frontend.dto.Banda.BandaResponse;

public record PersonaDetenidaResponse(
        Long id,
        String codigo,
        String nombre,
        BandaResponse banda
) {}
