package com.loanmanagement.emi.application.dto.response;

import com.loanmanagement.emi.domain.model.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmiPaymentResponseTest {

    @Test
    void shouldCreateEmiPaymentResponseWithBuilder() {
        LocalDate paymentDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();

        EmiPaymentResponse response = EmiPaymentResponse.builder()
                .id(1L)
                .emiScheduleId(10L)
                .loanId(100L)
                .emiNumber(5)
                .amount(BigDecimal.valueOf(4500))
                .paymentDate(paymentDate)
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference("TXN123")
                .paidBy(20L)
                .paidByName("John Doe")
                .remarks("EMI paid successfully")
                .createdAt(createdAt)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getEmiScheduleId());
        assertEquals(100L, response.getLoanId());
        assertEquals(5, response.getEmiNumber());
        assertEquals(BigDecimal.valueOf(4500), response.getAmount());
        assertEquals(paymentDate, response.getPaymentDate());
        assertEquals(PaymentMethod.CASH, response.getPaymentMethod());
        assertEquals("TXN123", response.getTransactionReference());
        assertEquals(20L, response.getPaidBy());
        assertEquals("John Doe", response.getPaidByName());
        assertEquals("EMI paid successfully", response.getRemarks());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        EmiPaymentResponse response = new EmiPaymentResponse();

        assertNull(response.getId());
        assertNull(response.getEmiScheduleId());
        assertNull(response.getLoanId());
        assertNull(response.getEmiNumber());
        assertNull(response.getAmount());
        assertNull(response.getPaymentDate());
        assertNull(response.getPaymentMethod());
        assertNull(response.getTransactionReference());
        assertNull(response.getPaidBy());
        assertNull(response.getPaidByName());
        assertNull(response.getRemarks());
        assertNull(response.getCreatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDate paymentDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();

        EmiPaymentResponse response = new EmiPaymentResponse(
                2L,
                11L,
                101L,
                6,
                BigDecimal.valueOf(5000),
                paymentDate,
                PaymentMethod.UPI,
                "TXN999",
                30L,
                "Jane Smith",
                "Paid via UPI",
                createdAt
        );

        assertEquals(2L, response.getId());
        assertEquals(11L, response.getEmiScheduleId());
        assertEquals(101L, response.getLoanId());
        assertEquals(6, response.getEmiNumber());
        assertEquals(BigDecimal.valueOf(5000), response.getAmount());
        assertEquals(paymentDate, response.getPaymentDate());
        assertEquals(PaymentMethod.UPI, response.getPaymentMethod());
        assertEquals("TXN999", response.getTransactionReference());
        assertEquals(30L, response.getPaidBy());
        assertEquals("Jane Smith", response.getPaidByName());
        assertEquals("Paid via UPI", response.getRemarks());
        assertEquals(createdAt, response.getCreatedAt());
    }
}
