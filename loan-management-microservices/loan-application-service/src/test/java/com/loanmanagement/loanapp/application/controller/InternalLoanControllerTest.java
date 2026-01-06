package com.loanmanagement.loanapp.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalLoanController.class)
class InternalLoanControllerTest {

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
        response.setLoanOfficerId(5L);
        response.setAmount(BigDecimal.valueOf(100000));
        response.setTenureMonths(24);
        response.setMonthlyIncome(BigDecimal.valueOf(50000));
        response.setPurpose("Home Loan");
        response.setAppliedAt(LocalDateTime.now());
        response.setAppliedDate(LocalDateTime.now());
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }

    @Test
    void getLoanById_success() throws Exception {
        Mockito.when(loanApplicationService.getLoanById(1L))
                .thenReturn(createLoanResponse(1L));

        mockMvc.perform(get("/api/internal/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(10L));
    }

    @Test
    void getTotalLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getAllLoans())
                .thenReturn(List.of(createLoanResponse(1L), createLoanResponse(2L)));

        mockMvc.perform(get("/api/internal/loans/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void getAllLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getAllLoans())
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/internal/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getRecentLoans_success() throws Exception {
        Mockito.when(loanApplicationService.getAllLoans())
                .thenReturn(List.of(createLoanResponse(1L), createLoanResponse(2L)));

        mockMvc.perform(get("/api/internal/loans/recent")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getLoansByStatusCount_success() throws Exception {
        Mockito.when(loanApplicationService.getLoansByStatus("APPROVED"))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/internal/loans/status/APPROVED/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void getLoansByOfficerId_success() throws Exception {
        Mockito.when(loanApplicationService.getLoansByOfficerId(5L))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/internal/loans/officer/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getLoanStatistics_success() throws Exception {
        Mockito.when(loanApplicationService.getAllLoans())
                .thenReturn(List.of(createLoanResponse(1L)));
        Mockito.when(loanApplicationService.getLoansByStatus(Mockito.anyString()))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/internal/loans/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLoans").value(1))
                .andExpect(jsonPath("$.approvedLoans", notNullValue()));
    }

    @Test
    void getLoansByCustomerId_success() throws Exception {
        Mockito.when(loanApplicationService.getCustomerLoans(10L))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/internal/loans/customer/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getLoansByStatus_success() throws Exception {
        Mockito.when(loanApplicationService.getLoansByStatus("PENDING"))
                .thenReturn(List.of(createLoanResponse(1L)));

        mockMvc.perform(get("/api/internal/loans/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void approveLoan_success() throws Exception {
        InternalLoanController.ApprovalRequest request =
                new InternalLoanController.ApprovalRequest();
        request.setApprovedAmount(BigDecimal.valueOf(90000));
        request.setInterestRate(BigDecimal.valueOf(8.5));

        mockMvc.perform(put("/api/internal/loans/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void rejectLoan_success() throws Exception {
        InternalLoanController.RejectionRequest request =
                new InternalLoanController.RejectionRequest();
        request.setReason("Low credit score");

        mockMvc.perform(put("/api/internal/loans/1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void disburseLoan_success() throws Exception {
        InternalLoanController.DisbursementRequest request =
                new InternalLoanController.DisbursementRequest();
        request.setDisbursementDate(LocalDate.now());
        request.setDisbursementMethod("NEFT");
        request.setReferenceNumber("REF123");

        mockMvc.perform(put("/api/internal/loans/1/disburse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void closeLoan_success() throws Exception {
        mockMvc.perform(put("/api/internal/loans/1/close"))
                .andExpect(status().isOk());
    }
}
