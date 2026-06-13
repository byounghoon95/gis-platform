package com.gisplatform.backend.analysis.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record LocationScoreResponse(
        Long locationId,
        int radiusMeters,
        BigDecimal footTrafficScore,
        BigDecimal transportScore,
        BigDecimal demandScore,
        BigDecimal competitionScore,
        BigDecimal rentScore,
        BigDecimal totalScore,
        String explanation,
        Instant calculatedAt
) {
}
