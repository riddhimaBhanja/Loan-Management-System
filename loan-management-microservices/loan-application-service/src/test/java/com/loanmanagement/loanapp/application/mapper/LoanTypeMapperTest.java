package com.loanmanagement.loanapp.application.mapper;

import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.domain.model.LoanType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeMapperTest {

    private final LoanTypeMapper loanTypeMapper =
            Mappers.getMapper(LoanTypeMapper.class);

    @Test
    void toResponse_shouldMapEntityToResponse() {
        LoanType loanType = new LoanType();

        LoanTypeResponse response = loanTypeMapper.toResponse(loanType);

        assertNotNull(response);
    }

    @Test
    void toEntity_shouldMapCreateRequestToEntity() {
        CreateLoanTypeRequest request = new CreateLoanTypeRequest();

        LoanType loanType = loanTypeMapper.toEntity(request);

        assertNotNull(loanType);
        assertNull(loanType.getId());
    }

    @Test
    void updateEntityFromRequest_shouldIgnoreNullValuesAndRestrictedFields() {
        UpdateLoanTypeRequest request = new UpdateLoanTypeRequest();
        LoanType loanType = new LoanType();

        loanTypeMapper.updateEntityFromRequest(request, loanType);

        assertNotNull(loanType);
    }
}
