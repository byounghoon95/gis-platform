package com.gisplatform.backend.location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record LocationRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 100) String businessType,
        @NotBlank @Size(max = 500) String address,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
        @Min(0) Integer rentPrice,
        String memo
) {
}
