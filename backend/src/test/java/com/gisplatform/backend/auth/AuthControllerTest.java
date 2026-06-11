package com.gisplatform.backend.auth;

import com.gisplatform.backend.auth.dto.AuthUserResponse;
import com.gisplatform.backend.auth.dto.LoginResponse;
import com.gisplatform.backend.api.error.GlobalExceptionHandler;
import com.gisplatform.backend.auth.security.DatabaseUserDetailsService;
import com.gisplatform.backend.auth.security.JsonSecurityErrorWriter;
import com.gisplatform.backend.auth.security.JwtProvider;
import com.gisplatform.backend.auth.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        JsonSecurityErrorWriter.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private DatabaseUserDetailsService userDetailsService;

    @Test
    void loginReturnsAccessToken() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse(
                "token",
                "Bearer",
                3600,
                new AuthUserResponse(1L, "admin@example.com", "Admin", "ADMIN")
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "admin1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }

    @Test
    void invalidCredentialsReturnUnauthorized() throws Exception {
        when(authService.login(any())).thenThrow(new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
