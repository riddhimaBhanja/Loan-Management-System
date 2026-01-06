package com.loanmanagement.emi.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.common.dto.GenerateEmiRequest;
import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.application.dto.response.EmiSummaryResponse;
import com.loanmanagement.emi.domain.service.EmiScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmiScheduleController.class)
@Import(TestSecurityConfig.class)
class EmiScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmiScheduleService emiScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGenerateEmiSchedule() throws Exception {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(12)
                .build();

        EmiScheduleResponse response = EmiScheduleResponse.builder()
                .loanId(1L)
                .emiNumber(1)
                .emiAmount(BigDecimal.valueOf(8791))
                .build();

        when(emiScheduleService.generateEmiSchedule(any(GenerateEmiRequest.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/emis/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].loanId").value(1L))
                .andExpect(jsonPath("$[0].emiNumber").value(1));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldGetEmiScheduleByLoanId() throws Exception {
        EmiScheduleResponse response = EmiScheduleResponse.builder()
                .loanId(2L)
                .emiNumber(1)
                .build();

        when(emiScheduleService.getEmiSchedule(2L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/emis/loan/{loanId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(2L));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldGetEmiSummary() throws Exception {
        EmiSummaryResponse summary = EmiSummaryResponse.builder()
                .loanId(3L)
                .paidAmount(BigDecimal.valueOf(20000))
                .outstandingAmount(BigDecimal.valueOf(80000))
                .build();

        when(emiScheduleService.getEmiSummary(3L))
                .thenReturn(summary);

        mockMvc.perform(get("/api/emis/loan/{loanId}/summary", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(3L))
                .andExpect(jsonPath("$.paidAmount").value(20000));
    }

    @Test
    @WithMockUser(roles = {"LOAN_OFFICER"})
    void shouldGetCustomerEmis() throws Exception {
        EmiScheduleResponse response = EmiScheduleResponse.builder()
                .customerId(10L)
                .emiNumber(1)
                .build();

        when(emiScheduleService.getEmiScheduleByCustomer(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/emis/customer/{customerId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(10L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetOverdueEmis() throws Exception {
        EmiScheduleResponse response = EmiScheduleResponse.builder()
                .emiNumber(2)
                .build();

        when(emiScheduleService.getOverdueEmis())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/emis/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emiNumber").value(2));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldGetCustomerOverdueEmis() throws Exception {
        EmiScheduleResponse response = EmiScheduleResponse.builder()
                .customerId(15L)
                .emiNumber(3)
                .build();

        when(emiScheduleService.getOverdueEmisByCustomer(15L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/emis/customer/{customerId}/overdue", 15L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(15L));
    }
}
