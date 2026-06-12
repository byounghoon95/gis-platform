package com.gisplatform.backend.analysis.dto;

import java.math.BigDecimal;

public record NearbyTransitStopResponse(
        Long id,
        String name,
        String type,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
