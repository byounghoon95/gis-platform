package com.gisplatform.backend.admin.upload;

import com.gisplatform.backend.gisdata.CompetitorRepository;
import com.gisplatform.backend.gisdata.Facility;
import com.gisplatform.backend.gisdata.FacilityRepository;
import com.gisplatform.backend.gisdata.FootTrafficSample;
import com.gisplatform.backend.gisdata.FootTrafficSampleRepository;
import com.gisplatform.backend.gisdata.TransitStopRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CsvUploadServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private TransitStopRepository transitStopRepository;

    @Mock
    private FootTrafficSampleRepository footTrafficSampleRepository;

    private CsvUploadService service;

    @BeforeEach
    void setUp() {
        service = new CsvUploadService(
                new CsvParser(),
                facilityRepository,
                competitorRepository,
                transitStopRepository,
                footTrafficSampleRepository
        );
    }

    @Test
    void validFacilitiesCsvInsertsRows() {
        CsvUploadResponse response = service.uploadFacilities(csv("""
                name,category,address,latitude,longitude
                Library,public,Seoul,37.4979,127.0276
                """));

        assertThat(response.totalRows()).isEqualTo(1);
        assertThat(response.insertedRows()).isEqualTo(1);
        assertThat(response.errors()).isEmpty();

        ArgumentCaptor<List<Facility>> captor = ArgumentCaptor.forClass(List.class);
        verify(facilityRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).singleElement()
                .satisfies(facility -> {
                    assertThat(facility.getName()).isEqualTo("Library");
                    assertThat(facility.getCategory()).isEqualTo("public");
                    assertThat(facility.getLatitude()).hasToString("37.4979");
                });
    }

    @Test
    void invalidCompetitorRowsReportBusinessTypeAndDoNotInsert() {
        CsvUploadResponse response = service.uploadCompetitors(csv("""
                name,business_type,address,latitude,longitude
                Rival,,Seoul,91,127.0276
                """));

        assertThat(response.insertedRows()).isZero();
        assertThat(response.errors()).extracting(CsvRowError::field)
                .contains("business_type", "latitude");
        verify(competitorRepository, never()).saveAll(anyList());
    }

    @Test
    void invalidTransitStopTypeIsReported() {
        CsvUploadResponse response = service.uploadTransitStops(csv("""
                name,type,latitude,longitude
                Station,TRAIN,37.4979,127.0276
                """));

        assertThat(response.errors()).singleElement()
                .satisfies(error -> {
                    assertThat(error.rowNumber()).isEqualTo(2);
                    assertThat(error.field()).isEqualTo("type");
                    assertThat(error.message()).isEqualTo("Value must be SUBWAY or BUS");
                });
        verify(transitStopRepository, never()).saveAll(anyList());
    }

    @Test
    void invalidFootTrafficFieldsAreReported() {
        CsvUploadResponse response = service.uploadFootTraffic(csv("""
                base_date,hour,latitude,longitude,count
                2026/01/01,24,37.4979,181,-1
                """));

        assertThat(response.errors()).extracting(CsvRowError::field)
                .containsExactly("base_date", "hour", "longitude", "count");
        verify(footTrafficSampleRepository, never()).saveAll(anyList());
    }

    @Test
    void missingRequiredColumnsAreReportedAndDoNotInsert() {
        CsvUploadResponse response = service.uploadFacilities(csv("""
                name,address,latitude
                Library,Seoul,37.4979
                """));

        assertThat(response.totalRows()).isEqualTo(1);
        assertThat(response.insertedRows()).isZero();
        assertThat(response.errors()).extracting(CsvRowError::field)
                .contains("category", "longitude");
        verify(facilityRepository, never()).saveAll(anyList());
    }

    @Test
    void validFootTrafficCsvInsertsRows() {
        CsvUploadResponse response = service.uploadFootTraffic(csv("""
                base_date,hour,latitude,longitude,count
                2026-01-01,13,37.4979,127.0276,1200
                """));

        assertThat(response.insertedRows()).isEqualTo(1);

        ArgumentCaptor<List<FootTrafficSample>> captor = ArgumentCaptor.forClass(List.class);
        verify(footTrafficSampleRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).singleElement()
                .satisfies(sample -> {
                    assertThat(sample.getBaseDate()).hasToString("2026-01-01");
                    assertThat(sample.getHour()).isEqualTo(13);
                    assertThat(sample.getCount()).isEqualTo(1200);
                });
    }

    private static MockMultipartFile csv(String content) {
        return new MockMultipartFile(
                "file",
                "upload.csv",
                "text/csv",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }
}
