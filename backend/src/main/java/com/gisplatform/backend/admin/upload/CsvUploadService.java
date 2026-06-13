package com.gisplatform.backend.admin.upload;

import com.gisplatform.backend.gisdata.Competitor;
import com.gisplatform.backend.gisdata.CompetitorRepository;
import com.gisplatform.backend.gisdata.Facility;
import com.gisplatform.backend.gisdata.FacilityRepository;
import com.gisplatform.backend.gisdata.FootTrafficSample;
import com.gisplatform.backend.gisdata.FootTrafficSampleRepository;
import com.gisplatform.backend.gisdata.TransitStop;
import com.gisplatform.backend.gisdata.TransitStopRepository;
import com.gisplatform.backend.gisdata.TransitStopType;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvUploadService {

    private final CsvParser csvParser;
    private final FacilityRepository facilityRepository;
    private final CompetitorRepository competitorRepository;
    private final TransitStopRepository transitStopRepository;
    private final FootTrafficSampleRepository footTrafficSampleRepository;

    public CsvUploadService(
            CsvParser csvParser,
            FacilityRepository facilityRepository,
            CompetitorRepository competitorRepository,
            TransitStopRepository transitStopRepository,
            FootTrafficSampleRepository footTrafficSampleRepository
    ) {
        this.csvParser = csvParser;
        this.facilityRepository = facilityRepository;
        this.competitorRepository = competitorRepository;
        this.transitStopRepository = transitStopRepository;
        this.footTrafficSampleRepository = footTrafficSampleRepository;
    }

    @Transactional
    public CsvUploadResponse uploadFacilities(MultipartFile file) {
        CsvTable table = parse(file);
        List<CsvRowError> errors = requireColumns(table, List.of("name", "category", "address", "latitude", "longitude"));
        List<FacilityCsvRow> validRows = new ArrayList<>();

        for (CsvRow row : table.rows()) {
            String name = required(row, "name", errors);
            String category = required(row, "category", errors);
            String address = required(row, "address", errors);
            BigDecimal latitude = latitude(row, errors);
            BigDecimal longitude = longitude(row, errors);

            if (hasValues(name, category, address, latitude, longitude)) {
                validRows.add(new FacilityCsvRow(name, category, address, latitude, longitude));
            }
        }

        List<Facility> facilities = validRows.stream()
                .map(row -> new Facility(row.name(), row.category(), row.address(), row.latitude(), row.longitude()))
                .toList();
        return persist(table.rows().size(), errors, facilities, facilityRepository);
    }

    @Transactional
    public CsvUploadResponse uploadCompetitors(MultipartFile file) {
        CsvTable table = parse(file);
        List<CsvRowError> errors = requireColumns(table, List.of("name", "business_type", "address", "latitude", "longitude"));
        List<CompetitorCsvRow> validRows = new ArrayList<>();

        for (CsvRow row : table.rows()) {
            String name = required(row, "name", errors);
            String businessType = required(row, "business_type", errors);
            String address = required(row, "address", errors);
            BigDecimal latitude = latitude(row, errors);
            BigDecimal longitude = longitude(row, errors);

            if (hasValues(name, businessType, address, latitude, longitude)) {
                validRows.add(new CompetitorCsvRow(name, businessType, address, latitude, longitude));
            }
        }

        List<Competitor> competitors = validRows.stream()
                .map(row -> new Competitor(row.name(), row.businessType(), row.address(), row.latitude(), row.longitude()))
                .toList();
        return persist(table.rows().size(), errors, competitors, competitorRepository);
    }

    @Transactional
    public CsvUploadResponse uploadTransitStops(MultipartFile file) {
        CsvTable table = parse(file);
        List<CsvRowError> errors = requireColumns(table, List.of("name", "type", "latitude", "longitude"));
        List<TransitStopCsvRow> validRows = new ArrayList<>();

        for (CsvRow row : table.rows()) {
            String name = required(row, "name", errors);
            TransitStopType type = transitStopType(row, errors);
            BigDecimal latitude = latitude(row, errors);
            BigDecimal longitude = longitude(row, errors);

            if (hasValues(name, type, latitude, longitude)) {
                validRows.add(new TransitStopCsvRow(name, type, latitude, longitude));
            }
        }

        List<TransitStop> transitStops = validRows.stream()
                .map(row -> new TransitStop(row.name(), row.type(), row.latitude(), row.longitude()))
                .toList();
        return persist(table.rows().size(), errors, transitStops, transitStopRepository);
    }

    @Transactional
    public CsvUploadResponse uploadFootTraffic(MultipartFile file) {
        CsvTable table = parse(file);
        List<CsvRowError> errors = requireColumns(table, List.of("base_date", "hour", "latitude", "longitude", "count"));
        List<FootTrafficCsvRow> validRows = new ArrayList<>();

        for (CsvRow row : table.rows()) {
            LocalDate baseDate = baseDate(row, errors);
            Integer hour = hour(row, errors);
            BigDecimal latitude = latitude(row, errors);
            BigDecimal longitude = longitude(row, errors);
            Integer count = count(row, errors);

            if (hasValues(baseDate, hour, latitude, longitude, count)) {
                validRows.add(new FootTrafficCsvRow(baseDate, hour, latitude, longitude, count));
            }
        }

        List<FootTrafficSample> samples = validRows.stream()
                .map(row -> new FootTrafficSample(row.baseDate(), row.hour(), row.latitude(), row.longitude(), row.count()))
                .toList();
        return persist(table.rows().size(), errors, samples, footTrafficSampleRepository);
    }

    private CsvTable parse(MultipartFile file) {
        try {
            return csvParser.parse(file.getInputStream());
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read CSV file", exception);
        }
    }

    private static List<CsvRowError> requireColumns(CsvTable table, List<String> requiredColumns) {
        List<CsvRowError> errors = new ArrayList<>();
        for (String column : requiredColumns) {
            if (!table.headers().contains(column)) {
                errors.add(new CsvRowError(1, column, "Required column is missing"));
            }
        }
        return errors;
    }

    private static String required(CsvRow row, String field, List<CsvRowError> errors) {
        String value = row.value(field);
        if (value.isBlank()) {
            errors.add(new CsvRowError(row.rowNumber(), field, "Required value is missing"));
            return null;
        }
        return value;
    }

    private static BigDecimal latitude(CsvRow row, List<CsvRowError> errors) {
        return decimalInRange(row, "latitude", new BigDecimal("-90"), new BigDecimal("90"), errors);
    }

    private static BigDecimal longitude(CsvRow row, List<CsvRowError> errors) {
        return decimalInRange(row, "longitude", new BigDecimal("-180"), new BigDecimal("180"), errors);
    }

    private static BigDecimal decimalInRange(
            CsvRow row,
            String field,
            BigDecimal minimum,
            BigDecimal maximum,
            List<CsvRowError> errors
    ) {
        String value = required(row, field, errors);
        if (value == null) {
            return null;
        }

        try {
            BigDecimal decimal = new BigDecimal(value);
            if (decimal.compareTo(minimum) < 0 || decimal.compareTo(maximum) > 0) {
                errors.add(new CsvRowError(row.rowNumber(), field, "Value must be between " + minimum + " and " + maximum));
                return null;
            }
            return decimal;
        } catch (NumberFormatException exception) {
            errors.add(new CsvRowError(row.rowNumber(), field, "Value must be a decimal number"));
            return null;
        }
    }

    private static TransitStopType transitStopType(CsvRow row, List<CsvRowError> errors) {
        String value = required(row, "type", errors);
        if (value == null) {
            return null;
        }

        try {
            return TransitStopType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            errors.add(new CsvRowError(row.rowNumber(), "type", "Value must be SUBWAY or BUS"));
            return null;
        }
    }

    private static LocalDate baseDate(CsvRow row, List<CsvRowError> errors) {
        String value = required(row, "base_date", errors);
        if (value == null) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            errors.add(new CsvRowError(row.rowNumber(), "base_date", "Value must be an ISO date"));
            return null;
        }
    }

    private static Integer hour(CsvRow row, List<CsvRowError> errors) {
        return integerInRange(row, "hour", 0, 23, errors);
    }

    private static Integer count(CsvRow row, List<CsvRowError> errors) {
        return integerInRange(row, "count", 0, Integer.MAX_VALUE, errors);
    }

    private static Integer integerInRange(CsvRow row, String field, int minimum, int maximum, List<CsvRowError> errors) {
        String value = required(row, field, errors);
        if (value == null) {
            return null;
        }

        try {
            int integer = Integer.parseInt(value);
            if (integer < minimum || integer > maximum) {
                errors.add(new CsvRowError(row.rowNumber(), field, "Value must be between " + minimum + " and " + maximum));
                return null;
            }
            return integer;
        } catch (NumberFormatException exception) {
            errors.add(new CsvRowError(row.rowNumber(), field, "Value must be an integer"));
            return null;
        }
    }

    private static boolean hasValues(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return false;
            }
        }
        return true;
    }

    private static <T> CsvUploadResponse persist(
            int totalRows,
            List<CsvRowError> errors,
            List<T> entities,
            org.springframework.data.jpa.repository.JpaRepository<T, Long> repository
    ) {
        if (!errors.isEmpty()) {
            return new CsvUploadResponse(totalRows, 0, List.copyOf(errors));
        }

        repository.saveAll(entities);
        return new CsvUploadResponse(totalRows, entities.size(), List.of());
    }

    private record FacilityCsvRow(
            String name,
            String category,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }

    private record CompetitorCsvRow(
            String name,
            String businessType,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }

    private record TransitStopCsvRow(
            String name,
            TransitStopType type,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }

    private record FootTrafficCsvRow(
            LocalDate baseDate,
            Integer hour,
            BigDecimal latitude,
            BigDecimal longitude,
            Integer count
    ) {
    }
}
