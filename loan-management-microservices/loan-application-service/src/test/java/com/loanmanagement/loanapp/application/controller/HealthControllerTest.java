package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HealthController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        com.loanmanagement.loanapp.infrastructure.config.JpaConfig.class
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.loanmanagement.loanapp.infrastructure.security.JwtUtil jwtUtil;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.loanmanagement.loanapp.infrastructure.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void publicHealth_success() throws Exception {
        mockMvc.perform(get("/api/health/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Service is running"))
                .andExpect(jsonPath("$.data.service").value("loan-application-service"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.timestamp", notNullValue()));
    }

    @Test
    void authenticatedHealth_success() throws Exception {
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken("testuser", null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/api/health/authenticated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Authentication verified"))
                .andExpect(jsonPath("$.data.service").value("loan-application-service"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.authenticated").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.timestamp", notNullValue()));

        SecurityContextHolder.clearContext();
    }
}
