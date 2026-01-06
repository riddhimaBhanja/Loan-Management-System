package com.loanmanagement.loanapplication.application.controller;

import com.loanmanagement.loanapp.application.controller.InternalLoanController;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalLoanController.class)
class InternalLoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    @Test
    void getLoanById_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(1L)                 // ✅ FIXED
                .customerId(10L)
                .amount(new BigDecimal("50000"))
                .status(LoanStatus.APPROVED)
                .build();

        when(loanApplicationService.getLoanById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/internal/loans/{loanId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(10L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getLoansByCustomerId_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(1L)                 // ✅ FIXED
                .customerId(10L)
                .amount(new BigDecimal("50000"))
                .status(LoanStatus.APPROVED)
                .build();

        when(loanApplicationService.getCustomerLoans(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/internal/loans/customer/{customerId}", 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].customerId").value(10L));
    }

    @Test
    void getLoansByStatus_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .id(2L)                 // ✅ FIXED
                .customerId(20L)
                .amount(new BigDecimal("75000"))
                .status(LoanStatus.APPROVED)
                .build();

        when(loanApplicationService.getLoansByStatus("PENDING"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/internal/loans/status/{status}", "PENDING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
