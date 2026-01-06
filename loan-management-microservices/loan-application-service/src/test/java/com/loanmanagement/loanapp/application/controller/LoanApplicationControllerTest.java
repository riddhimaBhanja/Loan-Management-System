package com.loanmanagement.loanapp.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import com.loanmanagement.loanapp.infrastructure.security.UserPrincipal;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanResponse createLoanResponse(Long id) {
        LoanResponse response = new LoanResponse();
        response.setId(id);
        response.setCustomerId(10L);
        response.setLoanTypeId(1L);
        response.setAmount(BigDecimal.valueOf(50000));
        response.setTenureMonths(12);
        response.setPurpose("Education");
        response.setAppliedAt(LocalDateTime.now());
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }

    @Test
    void createLoanApplication_success() throws Exception {
        LoanApplicationRequest request = new LoanApplicationRequest();

        Mockito.when(loanApplicationService.createLoanApplication(Mockito.any(), Mockito.eq(10L)))
                .thenReturn(createLoanResponse(1L));

        UserPrincipal userPrincipal = Mockito.mock(UserPrincipal.class);
        Mockito.when(userPrincipal.getUserId()).thenReturn(10L);
        Authentication authentication =
                new TestingAuthenticationToken(userPrincipal, null, "ROLE_CUSTOMER");

        mockMvc.perform(post("/api/loans/apply")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_APPLICATION_CREATED))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void getLoanById_success() throws Exception {
        Mockito.when(loanApplicationService.getLoanById(1L))
                .thenReturn(createLoanResponse(1L));

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_FETCHED))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void getMyLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getCustomerLoans(10L))
                .thenReturn(List.of(createLoanResponse(1L)));

        UserPrincipal userPrincipal = Mockito.mock(UserPrincipal.class);
        Mockito.when(userPrincipal.getUserId()).thenReturn(10L);
        Authentication authentication =
                new TestingAuthenticationToken(userPrincipal, null, "ROLE_CUSTOMER");

        mockMvc.perform(get("/api/loans/my-loans").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOANS_FETCHED))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getCustomerLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getCustomerLoans(20L))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/loans/customer/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOANS_FETCHED))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getAllLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getAllLoans())
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOANS_FETCHED))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getLoansByStatus_success() throws Exception {
        Mockito.when(loanApplicationService.getLoansByStatus("APPROVED"))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/loans/status/APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOANS_FETCHED))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
