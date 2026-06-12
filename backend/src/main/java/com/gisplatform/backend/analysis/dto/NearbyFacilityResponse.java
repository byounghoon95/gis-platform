package com.gisplatform.backend.analysis.dto;

import java.math.BigDecimal;

public record NearbyFacilityResponse(
        Long id,
        String name,
        String category,
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
