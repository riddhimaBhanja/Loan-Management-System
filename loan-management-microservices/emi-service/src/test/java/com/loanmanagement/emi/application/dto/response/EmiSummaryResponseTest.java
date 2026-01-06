package com.loanmanagement.emi.application.dto.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmiSummaryResponseTest {

    @Test
    void shouldCreateEmiSummaryResponseWithBuilder() {
        EmiSummaryResponse response = EmiSummaryResponse.builder()
                .loanId(100L)
                .customerId(10L)
                .totalEmis(24)
                .paidEmis(10)
                .pendingEmis(12)
                .overdueEmis(2)
                .totalAmount(BigDecimal.valueOf(240000))
                .paidAmount(BigDecimal.valueOf(100000))
                .pendingAmount(BigDecimal.valueOf(120000))
                .outstandingAmount(BigDecimal.valueOf(140000))
                .build();

        assertEquals(100L, response.getLoanId());
        assertEquals(10L, response.getCustomerId());
        assertEquals(24, response.getTotalEmis());
        assertEquals(10, response.getPaidEmis());
        assertEquals(12, response.getPendingEmis());
        assertEquals(2, response.getOverdueEmis());
        assertEquals(BigDecimal.valueOf(240000), response.getTotalAmount());
        assertEquals(BigDecimal.valueOf(100000), response.getPaidAmount());
        assertEquals(BigDecimal.valueOf(120000), response.getPendingAmount());
        assertEquals(BigDecimal.valueOf(140000), response.getOutstandingAmount());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        EmiSummaryResponse response = new EmiSummaryResponse();

        assertNull(response.getLoanId());
        assertNull(response.getCustomerId());
        assertNull(response.getTotalEmis());
        assertNull(response.getPaidEmis());
        assertNull(response.getPendingEmis());
        assertNull(response.getOverdueEmis());
        assertNull(response.getTotalAmount());
        assertNull(response.getPaidAmount());
        assertNull(response.getPendingAmount());
        assertNull(response.getOutstandingAmount());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        EmiSummaryResponse response = new EmiSummaryResponse(
                200L,
                20L,
                36,
                20,
                14,
                2,
                BigDecimal.valueOf(360000),
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(140000),
                BigDecimal.valueOf(160000)
        );

        assertEquals(200L, response.getLoanId());
        assertEquals(20L, response.getCustomerId());
        assertEquals(36, response.getTotalEmis());
        assertEquals(20, response.getPaidEmis());
        assertEquals(14, response.getPendingEmis());
        assertEquals(2, response.getOverdueEmis());
        assertEquals(BigDecimal.valueOf(360000), response.getTotalAmount());
        assertEquals(BigDecimal.valueOf(200000), response.getPaidAmount());
        assertEquals(BigDecimal.valueOf(140000), response.getPendingAmount());
        assertEquals(BigDecimal.valueOf(160000), response.getOutstandingAmount());
    }
}
