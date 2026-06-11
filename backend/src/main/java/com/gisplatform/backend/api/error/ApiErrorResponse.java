package com.gisplatform.backend.api.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        String path,
        int status,
        String error,
        String message,
        List<ApiFieldError> details
) {
}
