package com.loanmanagement.loanapplication.application.mapper;

import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;

import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.application.mapper.LoanMapper;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.model.Loan;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoanMapperTest {

    private final LoanMapper mapper = Mappers.getMapper(LoanMapper.class);

    @Test
    void shouldMapLoanToResponse() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setCustomerId(100L);
        loan.setLoanTypeId(2L);
        loan.setAmount(BigDecimal.valueOf(500000));
        loan.setTenureMonths(24);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setAppliedDate(LocalDateTime.now());

        LoanResponse response = mapper.toResponse(loan);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(loan.getId());
        assertThat(response.getCustomerId()).isEqualTo(loan.getCustomerId());
        assertThat(response.getLoanTypeId()).isEqualTo(loan.getLoanTypeId());
        assertThat(response.getAmount()).isEqualTo(loan.getAmount());
        assertThat(response.getTenureMonths()).isEqualTo(loan.getTenureMonths());
        assertThat(response.getStatus()).isEqualTo(loan.getStatus());
    }

    @Test
    void shouldMapLoanApplicationRequestToEntity() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanTypeId(3L);
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTenureMonths(36);

        Long customerId = 200L;

        Loan loan = mapper.toEntity(request, customerId);

        assertThat(loan).isNotNull();
        assertThat(loan.getId()).isNull();                 // ignored
        assertThat(loan.getCustomerId()).isEqualTo(customerId);
        assertThat(loan.getLoanTypeId()).isEqualTo(3L);
        assertThat(loan.getAmount()).isEqualTo(request.getAmount());
        assertThat(loan.getTenureMonths()).isEqualTo(request.getTenureMonths());
        assertThat(loan.getStatus()).isNull();             // ignored
        assertThat(loan.getAppliedDate()).isNull();        // ignored
        assertThat(loan.getCreatedAt()).isNull();          // ignored
        assertThat(loan.getUpdatedAt()).isNull();          // ignored
    }

    @Test
    void shouldReturnNullWhenLoanIsNull() {
        LoanResponse response = mapper.toResponse(null);
        assertThat(response).isNull();
    }
}
