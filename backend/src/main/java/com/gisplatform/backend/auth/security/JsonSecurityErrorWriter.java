package com.gisplatform.backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gisplatform.backend.api.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class JsonSecurityErrorWriter {

    private final ObjectMapper objectMapper;

    public JsonSecurityErrorWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase(),
                message,
                List.of()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
