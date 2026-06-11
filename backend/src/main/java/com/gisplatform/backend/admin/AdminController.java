package com.gisplatform.backend.admin;

import com.gisplatform.backend.auth.dto.AuthUserResponse;
import com.gisplatform.backend.auth.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> getMe(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(new AuthUserResponse(
                user.id(),
                user.getUsername(),
                user.name(),
                user.role()
        ));
    }
}
