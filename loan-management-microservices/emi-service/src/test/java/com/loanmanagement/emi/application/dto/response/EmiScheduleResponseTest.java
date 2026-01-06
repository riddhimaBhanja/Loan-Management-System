package com.loanmanagement.emi.application.dto.response;

import com.loanmanagement.emi.domain.model.EmiStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmiScheduleResponseTest {

    @Test
    void shouldCreateEmiScheduleResponseWithBuilder() {
        LocalDate dueDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        EmiScheduleResponse response = EmiScheduleResponse.builder()
                .id(1L)
                .loanId(100L)
                .customerId(10L)
                .emiNumber(1)
                .emiAmount(BigDecimal.valueOf(8500))
                .principalComponent(BigDecimal.valueOf(7000))
                .interestComponent(BigDecimal.valueOf(1500))
                .dueDate(dueDate)
                .outstandingBalance(BigDecimal.valueOf(93000))
                .status(EmiStatus.PAID)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(100L, response.getLoanId());
        assertEquals(10L, response.getCustomerId());
        assertEquals(1, response.getEmiNumber());
        assertEquals(BigDecimal.valueOf(8500), response.getEmiAmount());
        assertEquals(BigDecimal.valueOf(7000), response.getPrincipalComponent());
        assertEquals(BigDecimal.valueOf(1500), response.getInterestComponent());
        assertEquals(dueDate, response.getDueDate());
        assertEquals(BigDecimal.valueOf(93000), response.getOutstandingBalance());
        assertEquals(EmiStatus.PAID, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        EmiScheduleResponse response = new EmiScheduleResponse();

        assertNull(response.getId());
        assertNull(response.getLoanId());
        assertNull(response.getCustomerId());
        assertNull(response.getEmiNumber());
        assertNull(response.getEmiAmount());
        assertNull(response.getPrincipalComponent());
        assertNull(response.getInterestComponent());
        assertNull(response.getDueDate());
        assertNull(response.getOutstandingBalance());
        assertNull(response.getStatus());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDate dueDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        EmiScheduleResponse response = new EmiScheduleResponse(
                2L,
                200L,
                20L,
                2,
                BigDecimal.valueOf(9000),
                BigDecimal.valueOf(7500),
                BigDecimal.valueOf(1500),
                dueDate,
                BigDecimal.valueOf(84000),
                EmiStatus.PENDING,
                createdAt,
                updatedAt
        );

        assertEquals(2L, response.getId());
        assertEquals(200L, response.getLoanId());
        assertEquals(20L, response.getCustomerId());
        assertEquals(2, response.getEmiNumber());
        assertEquals(BigDecimal.valueOf(9000), response.getEmiAmount());
        assertEquals(BigDecimal.valueOf(7500), response.getPrincipalComponent());
        assertEquals(BigDecimal.valueOf(1500), response.getInterestComponent());
        assertEquals(dueDate, response.getDueDate());
        assertEquals(BigDecimal.valueOf(84000), response.getOutstandingBalance());
        assertEquals(EmiStatus.PENDING, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }
}
