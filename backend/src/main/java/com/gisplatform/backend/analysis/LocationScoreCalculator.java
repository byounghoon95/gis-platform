package com.gisplatform.backend.analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class LocationScoreCalculator {

    private static final BigDecimal FOOT_TRAFFIC_WEIGHT = new BigDecimal("0.30");
    private static final BigDecimal TRANSPORT_WEIGHT = new BigDecimal("0.25");
    private static final BigDecimal DEMAND_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal COMPETITION_WEIGHT = new BigDecimal("0.15");
    private static final BigDecimal RENT_WEIGHT = new BigDecimal("0.10");

    public ScoreBreakdown calculate(NearbySummary nearby, Integer rentPrice) {
        BigDecimal footTrafficScore = normalize(nearby.averageFootTraffic(), 200);
        BigDecimal transportScore = clamp(BigDecimal.valueOf(nearby.subwayStationCount() * 40L + nearby.busStopCount() * 8L));
        BigDecimal demandScore = clamp(BigDecimal.valueOf(nearby.demandFacilityCount() * 10L));
        BigDecimal competitionScore = clamp(BigDecimal.valueOf(100L - nearby.competitorCount() * 15L));
        BigDecimal rentScore = rentScore(rentPrice);
        BigDecimal totalScore = footTrafficScore.multiply(FOOT_TRAFFIC_WEIGHT)
                .add(transportScore.multiply(TRANSPORT_WEIGHT))
                .add(demandScore.multiply(DEMAND_WEIGHT))
                .add(competitionScore.multiply(COMPETITION_WEIGHT))
                .add(rentScore.multiply(RENT_WEIGHT));

        return new ScoreBreakdown(
                scale(footTrafficScore),
                scale(transportScore),
                scale(demandScore),
                scale(competitionScore),
                scale(rentScore),
                scale(clamp(totalScore))
        );
    }

    private static BigDecimal normalize(double value, int maxValue) {
        if (maxValue <= 0) {
            return BigDecimal.ZERO;
        }

        return clamp(BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(maxValue), 4, RoundingMode.HALF_UP));
    }

    private static BigDecimal rentScore(Integer rentPrice) {
        if (rentPrice == null) {
            return BigDecimal.valueOf(50);
        }
        if (rentPrice <= 1_000_000) {
            return BigDecimal.valueOf(100);
        }
        if (rentPrice >= 10_000_000) {
            return BigDecimal.ZERO;
        }

        BigDecimal range = BigDecimal.valueOf(9_000_000L);
        BigDecimal aboveMinimum = BigDecimal.valueOf(rentPrice - 1_000_000L);
        return clamp(BigDecimal.valueOf(100).subtract(
                aboveMinimum.multiply(BigDecimal.valueOf(100)).divide(range, 4, RoundingMode.HALF_UP)
        ));
    }

    private static BigDecimal clamp(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }
        return value;
    }

    private static BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
