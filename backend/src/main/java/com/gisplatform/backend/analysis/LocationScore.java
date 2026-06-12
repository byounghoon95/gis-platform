package com.gisplatform.backend.analysis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "location_scores")
public class LocationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private Integer radiusMeters;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal footTrafficScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal transportScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal demandScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal competitionScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rentScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(nullable = false, columnDefinition = "text")
    private String explanation;

    @Column(nullable = false, insertable = false, updatable = false)
    private Instant calculatedAt;

    protected LocationScore() {
    }

    public LocationScore(Long locationId, Integer radiusMeters, ScoreBreakdown score, String explanation) {
        this.locationId = locationId;
        this.radiusMeters = radiusMeters;
        this.footTrafficScore = score.footTrafficScore();
        this.transportScore = score.transportScore();
        this.demandScore = score.demandScore();
        this.competitionScore = score.competitionScore();
        this.rentScore = score.rentScore();
        this.totalScore = score.totalScore();
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Integer getRadiusMeters() {
        return radiusMeters;
    }

    public BigDecimal getFootTrafficScore() {
        return footTrafficScore;
    }

    public BigDecimal getTransportScore() {
        return transportScore;
    }

    public BigDecimal getDemandScore() {
        return demandScore;
    }

    public BigDecimal getCompetitionScore() {
        return competitionScore;
    }

    public BigDecimal getRentScore() {
        return rentScore;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
