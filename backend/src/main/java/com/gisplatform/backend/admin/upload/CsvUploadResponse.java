package com.gisplatform.backend.admin.upload;

import java.util.List;

public record CsvUploadResponse(
        int totalRows,
        int insertedRows,
        List<CsvRowError> errors
) {

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
