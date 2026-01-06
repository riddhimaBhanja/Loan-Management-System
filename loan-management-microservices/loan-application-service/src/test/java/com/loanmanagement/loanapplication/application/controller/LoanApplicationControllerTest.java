package com.loanmanagement.loanapplication.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.loanapp.application.controller.LoanApplicationController;
import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    @Test
    @WithMockUser(username = "10", roles = {"CUSTOMER"})
    void createLoanApplication_success() throws Exception {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(new BigDecimal("50000"))
                .tenureMonths(12)
                .build();

        LoanResponse response = LoanResponse.builder()
                .id(1L)
                .customerId(10L)
                .amount(new BigDecimal("50000"))
                .build();

        when(loanApplicationService.createLoanApplication(any(), eq(10L)))
                .thenReturn(response);

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(new TestingAuthenticationToken("10", null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_APPLICATION_CREATED))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.customerId").value(10L));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void getLoanById_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(1L)
                .customerId(10L)
                .amount(new BigDecimal("50000"))
                .status(LoanStatus.APPROVED)
                .build();

        when(loanApplicationService.getLoanById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/loans/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_FETCHED))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @WithMockUser(username = "10", roles = {"CUSTOMER"})
    void getMyLoans_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(1L)
                .customerId(10L)
                .amount(new BigDecimal("50000"))
                .status(LoanStatus.PENDING)
                .build();

        when(loanApplicationService.getCustomerLoans(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/loans/my-loans")
                        .principal(new TestingAuthenticationToken("10", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOANS_FETCHED))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"LOAN_OFFICER"})
    void getCustomerLoans_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(2L)
                .customerId(20L)
                .amount(new BigDecimal("75000"))
                .status(LoanStatus.APPROVED)
                .build();

        when(loanApplicationService.getCustomerLoans(20L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/loans/customer/{customerId}", 20L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerId").value(20L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllLoans_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(3L)
                .customerId(30L)
                .amount(new BigDecimal("90000"))
                .status(LoanStatus.APPROVED)
                .build();

        when(loanApplicationService.getAllLoans())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(3L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getLoansByStatus_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(4L)
                .customerId(40L)
                .amount(new BigDecimal("60000"))
                .status(LoanStatus.PENDING)
                .build();

        when(loanApplicationService.getLoansByStatus("PENDING"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/loans/status/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }
}
