package com.gisplatform.backend.api.error;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.ValidationProbeController.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void invalidRequestBodyUsesCommonErrorShape() throws Exception {
        mockMvc.perform(post("/api/test-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/test-validation"))
                .andExpect(jsonPath("$.details[0].field").value("name"));
    }

    @RestController
    @RequestMapping("/api/test-validation")
    static class ValidationProbeController {

        @PostMapping
        void validate(@Valid @RequestBody ValidationProbeRequest request) {
        }
    }

    record ValidationProbeRequest(
            @NotBlank String name
    ) {
    }
}
