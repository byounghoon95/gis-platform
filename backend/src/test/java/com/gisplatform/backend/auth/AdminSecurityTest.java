package com.gisplatform.backend.auth;

import com.gisplatform.backend.admin.AdminController;
import com.gisplatform.backend.auth.security.AuthenticatedUser;
import com.gisplatform.backend.auth.security.DatabaseUserDetailsService;
import com.gisplatform.backend.auth.security.JsonSecurityErrorWriter;
import com.gisplatform.backend.auth.security.JwtProvider;
import com.gisplatform.backend.auth.security.SecurityConfig;
import com.gisplatform.backend.auth.user.UserAccount;
import com.gisplatform.backend.auth.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({
        SecurityConfig.class,
        JsonSecurityErrorWriter.class
})
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private DatabaseUserDetailsService userDetailsService;

    @Test
    void anonymousUsersCannotCallAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void adminUsersCanCallAdminApi() throws Exception {
        AuthenticatedUser admin = new AuthenticatedUser(new UserAccount(
                "admin@example.com",
                "encoded",
                "Admin",
                UserRole.ADMIN
        ));

        mockMvc.perform(get("/api/admin/me").with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
