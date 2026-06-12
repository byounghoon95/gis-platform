package com.gisplatform.backend.gisdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "foot_traffic_samples")
public class FootTrafficSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate baseDate;

    @Column(nullable = false)
    private Integer hour;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    protected FootTrafficSample() {
    }

    public FootTrafficSample(LocalDate baseDate, Integer hour, BigDecimal latitude, BigDecimal longitude, Integer count) {
        this.baseDate = baseDate;
        this.hour = hour;
        this.latitude = latitude;
        this.longitude = longitude;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBaseDate() {
        return baseDate;
    }

    public Integer getHour() {
        return hour;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Integer getCount() {
        return count;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
