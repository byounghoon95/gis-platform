package com.gisplatform.backend.location;

import com.gisplatform.backend.api.error.GlobalExceptionHandler;
import com.gisplatform.backend.location.dto.LocationResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    void createReturnsCreatedLocation() throws Exception {
        when(locationService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/admin/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/admin/locations/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gangnam Cafe"))
                .andExpect(jsonPath("$.businessType").value("CAFE"));
    }

    @Test
    void createRejectsInvalidLatitude() throws Exception {
        mockMvc.perform(post("/api/admin/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Gangnam Cafe",
                                  "businessType": "CAFE",
                                  "address": "Seoul Gangnam",
                                  "latitude": 91.0,
                                  "longitude": 127.0276
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("latitude"));
    }

    @Test
    void listPassesQueryParametersToService() throws Exception {
        when(locationService.list("CAFE", 20, 80, "gangnam")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/admin/locations")
                        .param("businessType", "CAFE")
                        .param("minScore", "20")
                        .param("maxScore", "80")
                        .param("keyword", "gangnam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(locationService).list("CAFE", 20, 80, "gangnam");
    }

    @Test
    void listRejectsInvalidScoreRange() throws Exception {
        mockMvc.perform(get("/api/admin/locations")
                        .param("minScore", "90")
                        .param("maxScore", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detailReturnsLocation() throws Exception {
        when(locationService.get(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/admin/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Seoul Gangnam"));
    }

    @Test
    void updateReturnsLocation() throws Exception {
        when(locationService.update(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/api/admin/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/locations/1"))
                .andExpect(status().isNoContent());

        verify(locationService).delete(1L);
    }

    private String validRequestJson() {
        return """
                {
                  "name": "Gangnam Cafe",
                  "businessType": "CAFE",
                  "address": "Seoul Gangnam",
                  "latitude": 37.4979,
                  "longitude": 127.0276,
                  "rentPrice": 3000000,
                  "memo": "near station"
                }
                """;
    }

    private LocationResponse sampleResponse() {
        return new LocationResponse(
                1L,
                "Gangnam Cafe",
                "CAFE",
                "Seoul Gangnam",
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                3000000,
                "near station",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}
