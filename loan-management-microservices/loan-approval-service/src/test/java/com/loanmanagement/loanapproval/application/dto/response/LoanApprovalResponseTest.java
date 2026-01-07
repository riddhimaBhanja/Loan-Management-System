package com.loanmanagement.loanapproval.application.dto.response;

import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanApprovalResponseTest {

    @Test
    void builderAndGetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .id(1L)
                .loanId(10L)
                .approverId(100L)
                .approverName("Loan Officer")
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .approvedAmount(new BigDecimal("50000.00"))
                .interestRate(new BigDecimal("10.50"))
                .decisionDate(now)
                .rejectionReason(null)
                .notes("Approved after verification")
                .createdAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getLoanId());
        assertEquals(100L, response.getApproverId());
        assertEquals("Loan Officer", response.getApproverName());
        assertEquals(LoanApproval.ApprovalStatus.APPROVED, response.getStatus());
        assertEquals(new BigDecimal("50000.00"), response.getApprovedAmount());
        assertEquals(new BigDecimal("10.50"), response.getInterestRate());
        assertEquals(now, response.getDecisionDate());
        assertNull(response.getRejectionReason());
        assertEquals("Approved after verification", response.getNotes());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void noArgsConstructorAndSetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LoanApprovalResponse response = new LoanApprovalResponse();
        response.setId(2L);
        response.setLoanId(20L);
        response.setApproverId(200L);
        response.setApproverName("Admin");
        response.setStatus(LoanApproval.ApprovalStatus.REJECTED);
        response.setApprovedAmount(null);
        response.setInterestRate(null);
        response.setDecisionDate(now);
        response.setRejectionReason("Low credit score");
        response.setNotes("Rejected after risk analysis");
        response.setCreatedAt(now);

        assertEquals(2L, response.getId());
        assertEquals(20L, response.getLoanId());
        assertEquals(200L, response.getApproverId());
        assertEquals("Admin", response.getApproverName());
        assertEquals(LoanApproval.ApprovalStatus.REJECTED, response.getStatus());
        assertNull(response.getApprovedAmount());
        assertNull(response.getInterestRate());
        assertEquals(now, response.getDecisionDate());
        assertEquals("Low credit score", response.getRejectionReason());
        assertEquals("Rejected after risk analysis", response.getNotes());
        assertEquals(now, response.getCreatedAt());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LoanApprovalResponse r1 = LoanApprovalResponse.builder()
                .id(1L)
                .loanId(10L)
                .approverId(100L)
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .approvedAmount(new BigDecimal("50000"))
                .createdAt(now)
                .build();

        LoanApprovalResponse r2 = LoanApprovalResponse.builder()
                .id(1L)
                .loanId(10L)
                .approverId(100L)
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .approvedAmount(new BigDecimal("50000"))
                .createdAt(now)
                .build();

        LoanApprovalResponse r3 = LoanApprovalResponse.builder()
                .id(2L)
                .loanId(20L)
                .status(LoanApproval.ApprovalStatus.REJECTED)
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);

        // Lombok canEqual path
        assertTrue(r1.canEqual(r2));
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .id(1L)
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "not-a-loan-approval-response");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .id(5L)
                .approverName("Officer A")
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("LoanApprovalResponse"));
        assertTrue(value.contains("Officer A"));
        assertTrue(value.contains("APPROVED"));
    }

    @Test
    void canEqual_shouldReturnFalseForDifferentClass() {
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .id(1L)
                .build();

        assertFalse(response.canEqual("invalid-object"));
    }

}
