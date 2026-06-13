package com.gisplatform.backend.analysis;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationScoreCalculatorTest {

    private final LocationScoreCalculator calculator = new LocationScoreCalculator();

    @Test
    void calculateKeepsScoresBetweenZeroAndOneHundred() {
        NearbySummary nearby = new NearbySummary(
                300,
                100,
                200,
                200,
                500,
                10_000,
                20_000_000
        );

        ScoreBreakdown score = calculator.calculate(nearby, nearby.rentPrice());

        assertBetweenZeroAndOneHundred(score.footTrafficScore());
        assertBetweenZeroAndOneHundred(score.transportScore());
        assertBetweenZeroAndOneHundred(score.demandScore());
        assertBetweenZeroAndOneHundred(score.competitionScore());
        assertBetweenZeroAndOneHundred(score.rentScore());
        assertBetweenZeroAndOneHundred(score.totalScore());
    }

    @Test
    void calculateAppliesConfiguredWeights() {
        NearbySummary nearby = new NearbySummary(
                10,
                1,
                9,
                5,
                2,
                100,
                1_000_000
        );

        ScoreBreakdown score = calculator.calculate(nearby, nearby.rentPrice());

        assertThat(score.footTrafficScore()).isEqualByComparingTo("50.00");
        assertThat(score.transportScore()).isEqualByComparingTo("100.00");
        assertThat(score.demandScore()).isEqualByComparingTo("50.00");
        assertThat(score.competitionScore()).isEqualByComparingTo("70.00");
        assertThat(score.rentScore()).isEqualByComparingTo("100.00");
        assertThat(score.totalScore()).isEqualByComparingTo("70.50");
    }

    @Test
    void nullRentUsesNeutralRentScore() {
        NearbySummary nearby = new NearbySummary(0, 0, 0, 0, 0, 0, null);

        ScoreBreakdown score = calculator.calculate(nearby, nearby.rentPrice());

        assertThat(score.rentScore()).isEqualByComparingTo("50.00");
    }

    @Test
    void maximumInputsAreClampedToOneHundred() {
        NearbySummary nearby = new NearbySummary(
                1_000,
                10,
                100,
                100,
                0,
                1_000,
                500_000
        );

        ScoreBreakdown score = calculator.calculate(nearby, nearby.rentPrice());

        assertThat(score.footTrafficScore()).isEqualByComparingTo("100.00");
        assertThat(score.transportScore()).isEqualByComparingTo("100.00");
        assertThat(score.demandScore()).isEqualByComparingTo("100.00");
        assertThat(score.competitionScore()).isEqualByComparingTo("100.00");
        assertThat(score.rentScore()).isEqualByComparingTo("100.00");
        assertThat(score.totalScore()).isEqualByComparingTo("100.00");
    }

    @Test
    void minimumInputsAreClampedToZeroWhereApplicable() {
        NearbySummary nearby = new NearbySummary(
                0,
                0,
                0,
                0,
                100,
                0,
                10_000_000
        );

        ScoreBreakdown score = calculator.calculate(nearby, nearby.rentPrice());

        assertThat(score.footTrafficScore()).isEqualByComparingTo("0.00");
        assertThat(score.transportScore()).isEqualByComparingTo("0.00");
        assertThat(score.demandScore()).isEqualByComparingTo("0.00");
        assertThat(score.competitionScore()).isEqualByComparingTo("0.00");
        assertThat(score.rentScore()).isEqualByComparingTo("0.00");
        assertThat(score.totalScore()).isEqualByComparingTo("0.00");
    }

    private static void assertBetweenZeroAndOneHundred(BigDecimal value) {
        assertThat(value).isBetween(BigDecimal.ZERO, BigDecimal.valueOf(100));
    }
}
