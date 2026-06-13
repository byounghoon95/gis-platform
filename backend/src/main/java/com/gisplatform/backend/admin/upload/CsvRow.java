package com.gisplatform.backend.admin.upload;

import java.util.Map;

record CsvRow(int rowNumber, Map<String, String> values) {

    String value(String column) {
        return values.getOrDefault(column, "").trim();
    }
}
