package com.loanmanagement.loanapproval.application.mapper;

import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.domain.model.LoanDisbursement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for LoanDisbursement entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface LoanDisbursementMapper {

    @Mapping(target = "disbursedByName", ignore = true)
    LoanDisbursementResponse toResponse(LoanDisbursement disbursement);
}
