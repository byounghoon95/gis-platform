package com.gisplatform.backend.auth;

import com.gisplatform.backend.auth.dto.LoginRequest;
import com.gisplatform.backend.auth.dto.LoginResponse;
import com.gisplatform.backend.auth.security.JwtProvider;
import com.gisplatform.backend.auth.user.UserAccount;
import com.gisplatform.backend.auth.user.UserAccountRepository;
import com.gisplatform.backend.auth.user.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtProvider jwtProvider = new JwtProvider("test-jwt-secret-change-me-32chars-ok", 3600);
    private final AuthService authService = new AuthService(userAccountRepository, passwordEncoder, jwtProvider);

    @Test
    void validCredentialsReturnBearerToken() {
        UserAccount admin = new UserAccount(
                "admin@example.com",
                passwordEncoder.encode("admin1234"),
                "Admin",
                UserRole.ADMIN
        );
        when(userAccountRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        LoginResponse response = authService.login(new LoginRequest("admin@example.com", "admin1234"));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresInSeconds()).isEqualTo(3600);
        assertThat(response.user().email()).isEqualTo("admin@example.com");
        assertThat(response.user().role()).isEqualTo("ADMIN");
        assertThat(jwtProvider.getSubject(response.accessToken())).isEqualTo("admin@example.com");
    }

    @Test
    void invalidCredentialsReturnUnauthorized() {
        UserAccount admin = new UserAccount(
                "admin@example.com",
                passwordEncoder.encode("admin1234"),
                "Admin",
                UserRole.ADMIN
        );
        when(userAccountRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin@example.com", "wrong")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED");
    }
}
