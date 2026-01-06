package com.loanmanagement.loanapproval.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanApprovalTest {

    @Test
    void builderAndGetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LoanApproval approval = LoanApproval.builder()
                .id(1L)
                .loanId(10L)
                .approverId(100L)
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .approvedAmount(new BigDecimal("50000.00"))
                .interestRate(new BigDecimal("10.50"))
                .decisionDate(now)
                .rejectionReason(null)
                .notes("Approved after verification")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, approval.getId());
        assertEquals(10L, approval.getLoanId());
        assertEquals(100L, approval.getApproverId());
        assertEquals(LoanApproval.ApprovalStatus.APPROVED, approval.getStatus());
        assertEquals(new BigDecimal("50000.00"), approval.getApprovedAmount());
        assertEquals(new BigDecimal("10.50"), approval.getInterestRate());
        assertEquals(now, approval.getDecisionDate());
        assertNull(approval.getRejectionReason());
        assertEquals("Approved after verification", approval.getNotes());
        assertEquals(now, approval.getCreatedAt());
        assertEquals(now, approval.getUpdatedAt());
    }

    @Test
    void noArgsConstructorAndSetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LoanApproval approval = new LoanApproval();
        approval.setId(2L);
        approval.setLoanId(20L);
        approval.setApproverId(200L);
        approval.setStatus(LoanApproval.ApprovalStatus.REJECTED);
        approval.setApprovedAmount(null);
        approval.setInterestRate(null);
        approval.setDecisionDate(now);
        approval.setRejectionReason("Low credit score");
        approval.setNotes("Rejected after risk analysis");
        approval.setCreatedAt(now);
        approval.setUpdatedAt(now);

        assertEquals(2L, approval.getId());
        assertEquals(20L, approval.getLoanId());
        assertEquals(200L, approval.getApproverId());
        assertEquals(LoanApproval.ApprovalStatus.REJECTED, approval.getStatus());
        assertNull(approval.getApprovedAmount());
        assertNull(approval.getInterestRate());
        assertEquals(now, approval.getDecisionDate());
        assertEquals("Low credit score", approval.getRejectionReason());
        assertEquals("Rejected after risk analysis", approval.getNotes());
        assertEquals(now, approval.getCreatedAt());
        assertEquals(now, approval.getUpdatedAt());
    }

    @Test
    void prePersist_shouldInitializeDatesWhenNull() {
        LoanApproval approval = LoanApproval.builder()
                .loanId(30L)
                .approverId(300L)
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .build();

        approval.onCreate();

        assertNotNull(approval.getCreatedAt());
        assertNotNull(approval.getUpdatedAt());
        assertNotNull(approval.getDecisionDate());
    }

    @Test
    void preUpdate_shouldUpdateUpdatedAt() throws InterruptedException {
        LoanApproval approval = new LoanApproval();
        LocalDateTime initialTime = LocalDateTime.now();
        approval.setUpdatedAt(initialTime);

        Thread.sleep(5);
        approval.onUpdate();

        assertTrue(approval.getUpdatedAt().isAfter(initialTime));
    }
}
