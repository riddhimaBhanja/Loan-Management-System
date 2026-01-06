package com.loanmanagement.emi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmiPaymentTest {

    @Test
    void shouldCreateEmiPaymentWithBuilder() {
        LocalDate paymentDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();

        EmiPayment payment = EmiPayment.builder()
                .id(1L)
                .emiScheduleId(10L)
                .loanId(100L)
                .amount(BigDecimal.valueOf(5000))
                .paymentDate(paymentDate)
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference("TXN123")
                .paidBy(20L)
                .remarks("EMI paid")
                .createdAt(createdAt)
                .build();

        assertEquals(1L, payment.getId());
        assertEquals(10L, payment.getEmiScheduleId());
        assertEquals(100L, payment.getLoanId());
        assertEquals(BigDecimal.valueOf(5000), payment.getAmount());
        assertEquals(paymentDate, payment.getPaymentDate());
        assertEquals(PaymentMethod.CASH, payment.getPaymentMethod());
        assertEquals("TXN123", payment.getTransactionReference());
        assertEquals(20L, payment.getPaidBy());
        assertEquals("EMI paid", payment.getRemarks());
        assertEquals(createdAt, payment.getCreatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        EmiPayment payment = new EmiPayment();

        assertNull(payment.getId());
        assertNull(payment.getEmiScheduleId());
        assertNull(payment.getLoanId());
        assertNull(payment.getAmount());
        assertNotNull(payment.getLateFee());
        assertEquals(BigDecimal.ZERO, payment.getLateFee());
        assertNull(payment.getTotalPaid());
        assertNull(payment.getPaymentDate());
        assertNull(payment.getPaymentMethod());
        assertNull(payment.getTransactionReference());
        assertNull(payment.getPaidBy());
        assertNull(payment.getRemarks());
        assertNull(payment.getCreatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDate paymentDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();

        EmiPayment payment = new EmiPayment(
                2L,
                11L,
                101L,
                BigDecimal.valueOf(6000),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(6100),
                paymentDate,
                PaymentMethod.UPI,
                "REF999",
                30L,
                "Paid via UPI",
                createdAt
        );

        assertEquals(2L, payment.getId());
        assertEquals(11L, payment.getEmiScheduleId());
        assertEquals(101L, payment.getLoanId());
        assertEquals(BigDecimal.valueOf(6000), payment.getAmount());
        assertEquals(BigDecimal.valueOf(100), payment.getLateFee());
        assertEquals(BigDecimal.valueOf(6100), payment.getTotalPaid());
        assertEquals(paymentDate, payment.getPaymentDate());
        assertEquals(PaymentMethod.UPI, payment.getPaymentMethod());
        assertEquals("REF999", payment.getTransactionReference());
        assertEquals(30L, payment.getPaidBy());
        assertEquals("Paid via UPI", payment.getRemarks());
        assertEquals(createdAt, payment.getCreatedAt());
    }

    @Test
    void isPaymentValid_shouldReturnTrueForPositiveAmount() {
        EmiPayment payment = EmiPayment.builder()
                .amount(BigDecimal.valueOf(1000))
                .build();

        assertTrue(payment.isPaymentValid());
    }

    @Test
    void isPaymentValid_shouldReturnFalseForZeroOrNullAmount() {
        EmiPayment zeroAmountPayment = EmiPayment.builder()
                .amount(BigDecimal.ZERO)
                .build();

        EmiPayment nullAmountPayment = new EmiPayment();

        assertFalse(zeroAmountPayment.isPaymentValid());
        assertFalse(nullAmountPayment.isPaymentValid());
    }

    @Test
    void requiresTransactionReference_shouldDelegateToPaymentMethod() {
        EmiPayment upiPayment = EmiPayment.builder()
                .paymentMethod(PaymentMethod.UPI)
                .build();

        EmiPayment cashPayment = EmiPayment.builder()
                .paymentMethod(PaymentMethod.CASH)
                .build();

        assertTrue(upiPayment.requiresTransactionReference());
        assertFalse(cashPayment.requiresTransactionReference());
    }

    @Test
    void onCreate_shouldInitializeDatesWhenNull() {
        EmiPayment payment = new EmiPayment();
        payment.onCreate();

        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getPaymentDate());
    }

    @Test
    void onCreate_shouldNotOverrideExistingDates() {
        LocalDate paymentDate = LocalDate.of(2025, 1, 1);
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0);

        EmiPayment payment = EmiPayment.builder()
                .paymentDate(paymentDate)
                .createdAt(createdAt)
                .build();

        payment.onCreate();

        assertEquals(paymentDate, payment.getPaymentDate());
        assertEquals(createdAt, payment.getCreatedAt());
    }
}
