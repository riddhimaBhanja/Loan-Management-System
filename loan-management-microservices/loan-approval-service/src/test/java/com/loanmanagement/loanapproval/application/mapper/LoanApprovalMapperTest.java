package com.loanmanagement.loanapproval.application.mapper;

import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanApprovalMapperTest {

    private final LoanApprovalMapper mapper =
            Mappers.getMapper(LoanApprovalMapper.class);

    @Test
    void toResponse_shouldMapAllFieldsExceptApproverName() {
        LocalDateTime now = LocalDateTime.now();

        LoanApproval entity = LoanApproval.builder()
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
                .build();

        LoanApprovalResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getLoanId());
        assertEquals(100L, response.getApproverId());
        assertEquals(LoanApproval.ApprovalStatus.APPROVED, response.getStatus());
        assertEquals(new BigDecimal("50000.00"), response.getApprovedAmount());
        assertEquals(new BigDecimal("10.50"), response.getInterestRate());
        assertEquals(now, response.getDecisionDate());
        assertNull(response.getRejectionReason());
        assertEquals("Approved after verification", response.getNotes());
        assertEquals(now, response.getCreatedAt());

        // explicitly ignored field
        assertNull(response.getApproverName());
    }
}
