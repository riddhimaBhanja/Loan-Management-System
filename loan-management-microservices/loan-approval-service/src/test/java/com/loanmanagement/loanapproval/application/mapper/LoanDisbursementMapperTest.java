package com.loanmanagement.loanapproval.application.mapper;

import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.domain.model.LoanDisbursement;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanDisbursementMapperTest {

    private final LoanDisbursementMapper mapper =
            Mappers.getMapper(LoanDisbursementMapper.class);

    @Test
    void toResponse_shouldMapAllFieldsExceptDisbursedByName() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate disbursementDate = LocalDate.now();

        LoanDisbursement entity = LoanDisbursement.builder()
                .id(1L)
                .loanId(10L)
                .disbursedBy(100L)
                .amount(new BigDecimal("50000.00"))
                .disbursementDate(disbursementDate)
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("TXN123456")
                .remarks("Disbursed successfully")
                .createdAt(now)
                .build();

        LoanDisbursementResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getLoanId());
        assertEquals(100L, response.getDisbursedBy());
        assertEquals(new BigDecimal("50000.00"), response.getAmount());
        assertEquals(disbursementDate, response.getDisbursementDate());
        assertEquals("BANK_TRANSFER", response.getDisbursementMethod());
        assertEquals("TXN123456", response.getReferenceNumber());
        assertEquals("Disbursed successfully", response.getRemarks());
        assertEquals(now, response.getCreatedAt());

        // explicitly ignored field
        assertNull(response.getDisbursedByName());
    }
}
