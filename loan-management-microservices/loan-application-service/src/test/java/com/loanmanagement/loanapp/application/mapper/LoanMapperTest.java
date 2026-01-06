package com.loanmanagement.loanapp.application.mapper;

import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.model.Loan;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LoanMapperTest {

    private final LoanMapper loanMapper =
            Mappers.getMapper(LoanMapper.class);

    @Test
    void toResponse_shouldMapLoanToLoanResponse() {
        Loan loan = new Loan();

        LoanResponse response = loanMapper.toResponse(loan);

        assertNotNull(response);
    }

    @Test
    void toEntity_shouldMapRequestAndCustomerIdToLoan() {
        LoanApplicationRequest request = Mockito.mock(LoanApplicationRequest.class);
        Mockito.when(request.getLoanTypeId()).thenReturn(2L);

        Long customerId = 10L;

        Loan loan = loanMapper.toEntity(request, customerId);

        assertNotNull(loan);
        assertNull(loan.getId());                 // ignored
        assertEquals(customerId, loan.getCustomerId());
        assertEquals(2L, loan.getLoanTypeId());
    }
}
