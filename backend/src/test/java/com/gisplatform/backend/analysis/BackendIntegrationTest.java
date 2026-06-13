package com.gisplatform.backend.analysis;

import com.gisplatform.backend.gisdata.Competitor;
import com.gisplatform.backend.gisdata.CompetitorRepository;
import com.gisplatform.backend.gisdata.Facility;
import com.gisplatform.backend.gisdata.FacilityRepository;
import com.gisplatform.backend.gisdata.FootTrafficSample;
import com.gisplatform.backend.gisdata.FootTrafficSampleRepository;
import com.gisplatform.backend.gisdata.TransitStop;
import com.gisplatform.backend.gisdata.TransitStopRepository;
import com.gisplatform.backend.gisdata.TransitStopType;
import com.gisplatform.backend.location.Location;
import com.gisplatform.backend.location.LocationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(properties = "spring.flyway.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BackendIntegrationTest {

    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:16-3.4-alpine")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationScoreRepository locationScoreRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private TransitStopRepository transitStopRepository;

    @Autowired
    private FootTrafficSampleRepository footTrafficSampleRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void cleanDatabase() {
        locationScoreRepository.deleteAllInBatch();
        footTrafficSampleRepository.deleteAllInBatch();
        transitStopRepository.deleteAllInBatch();
        competitorRepository.deleteAllInBatch();
        facilityRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void locationCrudPersistsThroughHttpApi() throws Exception {
        String createResponse = mockMvc.perform(post("/api/admin/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Gangnam Cafe",
                                  "businessType": "CAFE",
                                  "address": "Seoul Gangnam",
                                  "latitude": 37.4979,
                                  "longitude": 127.0276,
                                  "rentPrice": 3000000,
                                  "memo": "near station"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Gangnam Cafe"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long locationId = idFrom(createResponse);

        mockMvc.perform(get("/api/admin/locations/{id}", locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessType").value("CAFE"));

        mockMvc.perform(put("/api/admin/locations/{id}", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Cafe",
                                  "businessType": "CAFE",
                                  "address": "Seoul Gangnam Updated",
                                  "latitude": 37.4980,
                                  "longitude": 127.0277,
                                  "rentPrice": 2500000,
                                  "memo": "updated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Cafe"))
                .andExpect(jsonPath("$.rentPrice").value(2500000));

        mockMvc.perform(get("/api/admin/locations")
                        .param("businessType", "CAFE")
                        .param("keyword", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(locationId));

        mockMvc.perform(delete("/api/admin/locations/{id}", locationId))
                .andExpect(status().isNoContent());
        assertThat(locationRepository.existsById(locationId)).isFalse();
    }

    @Test
    void postgisRadiusQueryReturnsOnlyNearbyFacilities() {
        facilityRepository.saveAndFlush(new Facility(
                "Nearby Library",
                "public",
                "Gangnam",
                decimal("37.4979"),
                decimal("127.0276")
        ));
        facilityRepository.saveAndFlush(new Facility(
                "Far Library",
                "public",
                "Jongno",
                decimal("37.5700"),
                decimal("126.9830")
        ));

        assertThat(facilityRepository.findWithinRadius(decimal("37.4979"), decimal("127.0276"), 300))
                .extracting(Facility::getName)
                .containsExactly("Nearby Library");
    }

    @Test
    void analysisApiCalculatesAndReturnsNearbyData() throws Exception {
        Location location = locationRepository.saveAndFlush(new Location(
                "Gangnam Cafe",
                "CAFE",
                "Seoul Gangnam",
                decimal("37.4979"),
                decimal("127.0276"),
                3_000_000,
                null
        ));
        seedNearbyData();

        mockMvc.perform(post("/api/locations/{locationId}/analysis", location.getId())
                        .param("radius", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationId").value(location.getId()))
                .andExpect(jsonPath("$.radiusMeters").value(500))
                .andExpect(jsonPath("$.footTrafficScore").value(75.00))
                .andExpect(jsonPath("$.transportScore").value(48.00))
                .andExpect(jsonPath("$.demandScore").value(10.00))
                .andExpect(jsonPath("$.competitionScore").value(85.00))
                .andExpect(jsonPath("$.rentScore").value(77.78))
                .andExpect(jsonPath("$.totalScore").value(57.03))
                .andExpect(jsonPath("$.explanation").value(org.hamcrest.Matchers.containsString("500m radius")));

        mockMvc.perform(get("/api/locations/{locationId}/score", location.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(57.03));

        mockMvc.perform(get("/api/locations/{locationId}/nearby", location.getId())
                        .param("radius", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.counts.transitStopCount").value(2))
                .andExpect(jsonPath("$.counts.demandFacilityCount").value(1))
                .andExpect(jsonPath("$.counts.competitorCount").value(1))
                .andExpect(jsonPath("$.footTraffic.averageCount").value(150.0))
                .andExpect(jsonPath("$.transitStops[0].type").value("BUS"))
                .andExpect(jsonPath("$.transitStops[1].type").value("SUBWAY"));
    }

    private void seedNearbyData() {
        facilityRepository.save(new Facility("Office Tower", "office", "Gangnam", decimal("37.4980"), decimal("127.0276")));
        competitorRepository.save(new Competitor("Rival Cafe", "CAFE", "Gangnam", decimal("37.4981"), decimal("127.0276")));
        transitStopRepository.save(new TransitStop("Gangnam Station", TransitStopType.SUBWAY, decimal("37.4979"), decimal("127.0276")));
        transitStopRepository.save(new TransitStop("Gangnam Bus", TransitStopType.BUS, decimal("37.4982"), decimal("127.0276")));
        footTrafficSampleRepository.save(new FootTrafficSample(LocalDate.parse("2026-01-01"), 12, decimal("37.4979"), decimal("127.0276"), 100));
        footTrafficSampleRepository.save(new FootTrafficSample(LocalDate.parse("2026-01-01"), 13, decimal("37.4979"), decimal("127.0276"), 200));
        footTrafficSampleRepository.flush();
    }

    private static BigDecimal decimal(String value) {
        return new BigDecimal(value);
    }

    private static long idFrom(String json) {
        String field = "\"id\":";
        int start = json.indexOf(field) + field.length();
        int end = json.indexOf(',', start);
        return Long.parseLong(json.substring(start, end).trim());
    }
}
