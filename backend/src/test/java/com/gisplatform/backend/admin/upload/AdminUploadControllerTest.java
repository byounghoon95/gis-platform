package com.gisplatform.backend.admin.upload;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvUploadService csvUploadService;

    @Test
    void uploadFacilitiesReturnsOkWhenCsvIsValid() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "facilities.csv", "text/csv", new byte[0]);
        when(csvUploadService.uploadFacilities(file))
                .thenReturn(new CsvUploadResponse(1, 1, List.of()));

        mockMvc.perform(multipart("/api/admin/uploads/facilities").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(1))
                .andExpect(jsonPath("$.insertedRows").value(1));
    }

    @Test
    void uploadTransitStopsReturnsBadRequestWhenCsvHasRowErrors() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "transit.csv", "text/csv", new byte[0]);
        when(csvUploadService.uploadTransitStops(file))
                .thenReturn(new CsvUploadResponse(
                        1,
                        0,
                        List.of(new CsvRowError(2, "type", "Value must be SUBWAY or BUS"))
                ));

        mockMvc.perform(multipart("/api/admin/uploads/transit-stops").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].rowNumber").value(2))
                .andExpect(jsonPath("$.errors[0].field").value("type"));
    }
}
