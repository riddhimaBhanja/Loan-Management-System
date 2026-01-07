package com.loanmanagement.emi.application.controller;

import com.loanmanagement.emi.domain.service.EmiScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalEmiController.class)
@Import(TestSecurityConfig.class)
class InternalEmiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmiScheduleService emiScheduleService;

    @Test
    void shouldReturnTrueWhenAllEmisPaid() throws Exception {
        when(emiScheduleService.verifyAllEmisPaid(1L))
                .thenReturn(true);

        mockMvc.perform(get("/api/internal/emis/loan/{loanId}/all-paid", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldReturnOutstandingAmount() throws Exception {
        when(emiScheduleService.getOutstandingAmount(2L))
                .thenReturn(BigDecimal.valueOf(25000));

        mockMvc.perform(get("/api/internal/emis/loan/{loanId}/outstanding", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("25000"));
    }

    @Test
    void shouldMarkOverdueEmis() throws Exception {
        when(emiScheduleService.markOverdueEmis())
                .thenReturn(4);

        mockMvc.perform(get("/api/internal/emis/mark-overdue")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }
}
