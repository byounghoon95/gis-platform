package com.gisplatform.backend.auth.security;

import com.gisplatform.backend.auth.user.UserAccount;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

    private final UserAccount userAccount;

    public AuthenticatedUser(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Long id() {
        return userAccount.getId();
    }

    public String name() {
        return userAccount.getName();
    }

    public String role() {
        return userAccount.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()));
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getEmail();
    }
}
