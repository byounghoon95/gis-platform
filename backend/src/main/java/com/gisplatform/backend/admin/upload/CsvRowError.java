package com.gisplatform.backend.admin.upload;

public record CsvRowError(int rowNumber, String field, String message) {
}
