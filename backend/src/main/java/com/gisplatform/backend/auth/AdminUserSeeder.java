package com.gisplatform.backend.auth;

import com.gisplatform.backend.auth.user.UserAccount;
import com.gisplatform.backend.auth.user.UserAccountRepository;
import com.gisplatform.backend.auth.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminName;

    public AdminUserSeeder(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.auth.admin-email}") String adminEmail,
            @Value("${app.auth.admin-password}") String adminPassword,
            @Value("${app.auth.admin-name}") String adminName
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userAccountRepository.existsByEmail(adminEmail)) {
            return;
        }

        userAccountRepository.save(new UserAccount(
                adminEmail,
                passwordEncoder.encode(adminPassword),
                adminName,
                UserRole.ADMIN
        ));
    }
}
