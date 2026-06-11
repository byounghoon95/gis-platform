package com.gisplatform.backend.location.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record LocationResponse(
        Long id,
        String name,
        String businessType,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer rentPrice,
        String memo,
        Instant createdAt,
        Instant updatedAt
) {
}
