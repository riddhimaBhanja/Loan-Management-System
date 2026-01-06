package com.loanmanagement.loanapp.application.dto.response;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanResponseTest {

    @Test
    void shouldCreateLoanResponseWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        LoanResponse response = LoanResponse.builder()
                .id(1L)
                .customerId(100L)
                .loanTypeId(10L)
                .loanOfficerId(5L)
                .amount(BigDecimal.valueOf(500000))
                .tenureMonths(120)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(60000))
                .purpose("Home purchase")
                .status(LoanStatus.PENDING)
                .appliedDate(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(100L, response.getCustomerId());
        assertEquals(10L, response.getLoanTypeId());
        assertEquals(5L, response.getLoanOfficerId());
        assertEquals(BigDecimal.valueOf(500000), response.getAmount());
        assertEquals(120, response.getTenureMonths());
        assertEquals(EmploymentStatus.SALARIED, response.getEmploymentStatus());
        assertEquals(BigDecimal.valueOf(60000), response.getMonthlyIncome());
        assertEquals("Home purchase", response.getPurpose());
        assertEquals(LoanStatus.PENDING, response.getStatus());
        assertEquals(now, response.getAppliedDate());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        LoanResponse response = new LoanResponse();

        assertNull(response.getId());
        assertNull(response.getCustomerId());
        assertNull(response.getLoanTypeId());
        assertNull(response.getLoanOfficerId());
        assertNull(response.getAmount());
        assertNull(response.getTenureMonths());
        assertNull(response.getEmploymentStatus());
        assertNull(response.getMonthlyIncome());
        assertNull(response.getPurpose());
        assertNull(response.getStatus());
        assertNull(response.getAppliedDate());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        LoanResponse response = new LoanResponse(
                2L,                              // id
                200L,                            // customerId
                "John Doe",                      // customerName
                20L,                             // loanTypeId
                "Personal Loan",                 // loanTypeName
                null,                            // loanOfficerId
                "LN-2024-0002",                  // applicationNumber
                BigDecimal.valueOf(750000),      // amount
                BigDecimal.valueOf(750000),      // requestedAmount
                180,                             // tenureMonths
                EmploymentStatus.SELF_EMPLOYED,  // employmentStatus
                BigDecimal.valueOf(90000),       // monthlyIncome
                "Business expansion",            // purpose
                LoanStatus.APPROVED,             // status
                now,                             // appliedDate
                now,                             // appliedAt
                now,                             // createdAt
                now                              // updatedAt
        );

        assertEquals(2L, response.getId());
        assertEquals(200L, response.getCustomerId());
        assertEquals("John Doe", response.getCustomerName());
        assertEquals(20L, response.getLoanTypeId());
        assertEquals("Personal Loan", response.getLoanTypeName());
        assertNull(response.getLoanOfficerId());
        assertEquals("LN-2024-0002", response.getApplicationNumber());
        assertEquals(BigDecimal.valueOf(750000), response.getAmount());
        assertEquals(BigDecimal.valueOf(750000), response.getRequestedAmount());
        assertEquals(180, response.getTenureMonths());
        assertEquals(EmploymentStatus.SELF_EMPLOYED, response.getEmploymentStatus());
        assertEquals(BigDecimal.valueOf(90000), response.getMonthlyIncome());
        assertEquals("Business expansion", response.getPurpose());
        assertEquals(LoanStatus.APPROVED, response.getStatus());
        assertEquals(now, response.getAppliedDate());
        assertEquals(now, response.getAppliedAt());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
}
