package com.gisplatform.backend.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String businessType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    private Integer rentPrice;

    @Column(columnDefinition = "text")
    private String memo;

    @Column(nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    protected Location() {
    }

    public Location(
            String name,
            String businessType,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            Integer rentPrice,
            String memo
    ) {
        update(name, businessType, address, latitude, longitude, rentPrice, memo);
    }

    public void update(
            String name,
            String businessType,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            Integer rentPrice,
            String memo
    ) {
        this.name = name;
        this.businessType = businessType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rentPrice = rentPrice;
        this.memo = memo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Integer getRentPrice() {
        return rentPrice;
    }

    public String getMemo() {
        return memo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
