package com.gisplatform.backend.analysis;

import java.math.BigDecimal;

public record ScoreBreakdown(
        BigDecimal footTrafficScore,
        BigDecimal transportScore,
        BigDecimal demandScore,
        BigDecimal competitionScore,
        BigDecimal rentScore,
        BigDecimal totalScore
) {
}
