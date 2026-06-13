package com.gisplatform.backend.analysis.dto;

import java.math.BigDecimal;

public record NearbyCompetitorResponse(
        Long id,
        String name,
        String businessType,
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
