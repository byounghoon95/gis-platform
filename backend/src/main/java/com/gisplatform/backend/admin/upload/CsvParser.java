package com.gisplatform.backend.admin.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

    CsvTable parse(InputStream inputStream) {
        String content;
        try {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read CSV file", exception);
        }

        List<List<String>> records = parseRecords(content);
        if (records.isEmpty()) {
            return new CsvTable(List.of(), List.of());
        }

        List<String> headers = records.get(0).stream()
                .map(CsvParser::normalizeHeader)
                .toList();

        List<CsvRow> rows = new ArrayList<>();
        for (int index = 1; index < records.size(); index++) {
            List<String> record = records.get(index);
            if (isBlankRecord(record)) {
                continue;
            }

            Map<String, String> values = new LinkedHashMap<>();
            for (int column = 0; column < headers.size(); column++) {
                String value = column < record.size() ? record.get(column) : "";
                values.put(headers.get(column), value);
            }
            rows.add(new CsvRow(index + 1, values));
        }

        return new CsvTable(headers, rows);
    }

    private static List<List<String>> parseRecords(String content) {
        List<List<String>> records = new ArrayList<>();
        List<String> currentRecord = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int index = 0; index < content.length(); index++) {
            char character = content.charAt(index);
            if (character == '"') {
                if (inQuotes && index + 1 < content.length() && content.charAt(index + 1) == '"') {
                    currentValue.append('"');
                    index++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (character == ',' && !inQuotes) {
                currentRecord.add(currentValue.toString());
                currentValue.setLength(0);
            } else if ((character == '\n' || character == '\r') && !inQuotes) {
                currentRecord.add(currentValue.toString());
                currentValue.setLength(0);
                records.add(currentRecord);
                currentRecord = new ArrayList<>();
                if (character == '\r' && index + 1 < content.length() && content.charAt(index + 1) == '\n') {
                    index++;
                }
            } else {
                currentValue.append(character);
            }
        }

        if (!currentRecord.isEmpty() || currentValue.length() > 0) {
            currentRecord.add(currentValue.toString());
            records.add(currentRecord);
        }

        return records;
    }

    private static boolean isBlankRecord(List<String> record) {
        return record.stream().allMatch(value -> value == null || value.isBlank());
    }

    private static String normalizeHeader(String header) {
        return header.trim().toLowerCase(Locale.ROOT);
    }
}
