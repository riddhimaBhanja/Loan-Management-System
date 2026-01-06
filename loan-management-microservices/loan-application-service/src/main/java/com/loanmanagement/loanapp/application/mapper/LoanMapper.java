package com.loanmanagement.loanapp.application.mapper;

import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Loan entity
 */
@Mapper(componentModel = "spring")
public interface LoanMapper {

    LoanResponse toResponse(Loan loan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "loanTypeId", source = "request.loanTypeId")
    @Mapping(target = "loanOfficerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "appliedDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Loan toEntity(LoanApplicationRequest request, Long customerId);
}
