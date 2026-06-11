package com.gisplatform.backend.auth;

import com.gisplatform.backend.auth.dto.AuthUserResponse;
import com.gisplatform.backend.auth.dto.LoginRequest;
import com.gisplatform.backend.auth.dto.LoginResponse;
import com.gisplatform.backend.auth.security.AuthenticatedUser;
import com.gisplatform.backend.auth.security.JwtProvider;
import com.gisplatform.backend.auth.user.UserAccount;
import com.gisplatform.backend.auth.user.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse login(LoginRequest request) {
        UserAccount userAccount = userAccountRepository.findByEmail(request.email())
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(request.password(), userAccount.getPassword())) {
            throw invalidCredentials();
        }

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userAccount);
        String accessToken = jwtProvider.createToken(authenticatedUser);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtProvider.getExpirationSeconds(),
                new AuthUserResponse(
                        userAccount.getId(),
                        userAccount.getEmail(),
                        userAccount.getName(),
                        userAccount.getRole().name()
                )
        );
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
