package com.loanmanagement.emi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmiScheduleTest {

    @Test
    void shouldCreateEmiScheduleWithBuilderAndDefaultStatus() {
        EmiSchedule schedule = EmiSchedule.builder()
                .loanId(100L)
                .customerId(10L)
                .emiNumber(1)
                .emiAmount(BigDecimal.valueOf(8500))
                .principalComponent(BigDecimal.valueOf(7000))
                .interestComponent(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(10))
                .outstandingBalance(BigDecimal.valueOf(93000))
                .build();

        assertEquals(EmiStatus.PENDING, schedule.getStatus());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        EmiSchedule schedule = new EmiSchedule();

        assertNull(schedule.getId());
        assertNull(schedule.getLoanId());
        assertNull(schedule.getCustomerId());
        assertNull(schedule.getEmiNumber());
        assertNull(schedule.getEmiAmount());
        assertNull(schedule.getPrincipalComponent());
        assertNull(schedule.getInterestComponent());
        assertNull(schedule.getDueDate());
        assertNull(schedule.getOutstandingBalance());
        assertEquals(EmiStatus.PENDING, schedule.getStatus());
        assertNull(schedule.getCreatedAt());
        assertNull(schedule.getUpdatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate dueDate = LocalDate.now().plusDays(5);

        EmiSchedule schedule = new EmiSchedule(
                1L,
                200L,
                20L,
                2,
                BigDecimal.valueOf(9000),
                BigDecimal.valueOf(7500),
                BigDecimal.valueOf(1500),
                dueDate,
                BigDecimal.valueOf(84000),
                EmiStatus.PENDING,
                now,
                now
        );

        assertEquals(1L, schedule.getId());
        assertEquals(200L, schedule.getLoanId());
        assertEquals(20L, schedule.getCustomerId());
        assertEquals(2, schedule.getEmiNumber());
        assertEquals(EmiStatus.PENDING, schedule.getStatus());
        assertEquals(now, schedule.getCreatedAt());
        assertEquals(now, schedule.getUpdatedAt());
    }

    @Test
    void onCreate_shouldInitializeCreatedAndUpdatedAtWhenNull() {
        EmiSchedule schedule = EmiSchedule.builder()
                .loanId(1L)
                .customerId(1L)
                .emiNumber(1)
                .emiAmount(BigDecimal.valueOf(1000))
                .principalComponent(BigDecimal.valueOf(800))
                .interestComponent(BigDecimal.valueOf(200))
                .dueDate(LocalDate.now().plusDays(5))
                .outstandingBalance(BigDecimal.valueOf(9000))
                .build();

        schedule.onCreate();

        assertNotNull(schedule.getCreatedAt());
        assertNotNull(schedule.getUpdatedAt());
    }

    @Test
    void onUpdate_shouldUpdateUpdatedAt() {
        EmiSchedule schedule = new EmiSchedule();
        schedule.setUpdatedAt(LocalDateTime.of(2025, 1, 1, 10, 0));

        schedule.onUpdate();

        assertTrue(schedule.getUpdatedAt().isAfter(
                LocalDateTime.of(2025, 1, 1, 10, 0)
        ));
    }

    @Test
    void markAsPaid_shouldChangeStatusToPaid() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PENDING)
                .build();

        schedule.markAsPaid();

        assertEquals(EmiStatus.PAID, schedule.getStatus());
    }

    @Test
    void markAsPaid_shouldThrowExceptionWhenInvalidState() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PAID)
                .build();

        assertThrows(IllegalStateException.class, schedule::markAsPaid);
    }

    @Test
    void markAsOverdue_shouldMarkOverdueWhenPastDueDate() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PENDING)
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        schedule.markAsOverdue();

        assertEquals(EmiStatus.OVERDUE, schedule.getStatus());
    }

    @Test
    void markAsOverdue_shouldNotChangeStatusWhenNotPastDue() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PENDING)
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        schedule.markAsOverdue();

        assertEquals(EmiStatus.PENDING, schedule.getStatus());
    }

    @Test
    void markAsPartialPaid_shouldAllowFromPending() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PENDING)
                .build();

        schedule.markAsPartialPaid();

        assertEquals(EmiStatus.PARTIAL_PAID, schedule.getStatus());
    }

    @Test
    void markAsPartialPaid_shouldAllowFromOverdue() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.OVERDUE)
                .build();

        schedule.markAsPartialPaid();

        assertEquals(EmiStatus.PARTIAL_PAID, schedule.getStatus());
    }

    @Test
    void isOverdue_shouldReturnTrueForOverdueStatus() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.OVERDUE)
                .build();

        assertTrue(schedule.isOverdue());
    }

    @Test
    void isOverdue_shouldReturnTrueWhenPendingAndPastDueDate() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PENDING)
                .dueDate(LocalDate.now().minusDays(2))
                .build();

        assertTrue(schedule.isOverdue());
    }

    @Test
    void isOverdue_shouldReturnFalseWhenPendingAndNotPastDueDate() {
        EmiSchedule schedule = EmiSchedule.builder()
                .status(EmiStatus.PENDING)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        assertFalse(schedule.isOverdue());
    }
}
