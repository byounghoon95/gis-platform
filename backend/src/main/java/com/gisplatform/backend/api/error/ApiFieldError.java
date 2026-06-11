package com.gisplatform.backend.api.error;

public record ApiFieldError(
        String field,
        String message
) {
}
