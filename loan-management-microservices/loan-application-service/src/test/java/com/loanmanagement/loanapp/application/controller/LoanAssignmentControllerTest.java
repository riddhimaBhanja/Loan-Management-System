package com.loanmanagement.loanapp.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.loanapp.application.dto.request.AssignOfficerRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoanAssignmentController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        com.loanmanagement.loanapp.infrastructure.config.JpaConfig.class
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class LoanAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    @MockBean
    private com.loanmanagement.loanapp.infrastructure.security.JwtUtil jwtUtil;

    @MockBean
    private com.loanmanagement.loanapp.infrastructure.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void assignOfficer_success() throws Exception {
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setLoanOfficerId(5L);

        Mockito.when(loanApplicationService.assignOfficer(Mockito.eq(1L), Mockito.any()))
                .thenReturn(new LoanResponse());

        Authentication authentication =
                new TestingAuthenticationToken("admin", null, "ROLE_ADMIN");

        mockMvc.perform(put("/api/loans/1/assign-officer")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Loan officer assigned successfully"))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void unassignOfficer_success() throws Exception {
        Mockito.when(loanApplicationService.unassignOfficer(1L))
                .thenReturn(new LoanResponse());

        Authentication authentication =
                new TestingAuthenticationToken("admin", null, "ROLE_ADMIN");

        mockMvc.perform(put("/api/loans/1/unassign-officer")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Loan officer unassigned successfully"))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void getAssignedLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getLoansByOfficerId(5L))
                .thenReturn(List.of(new LoanResponse()));

        mockMvc.perform(get("/api/loans/officer/5/assigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Assigned loans retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
