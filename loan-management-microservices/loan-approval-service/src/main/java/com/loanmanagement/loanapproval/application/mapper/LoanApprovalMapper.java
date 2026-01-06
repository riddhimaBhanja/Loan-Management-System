package com.loanmanagement.loanapproval.application.mapper;

import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for LoanApproval entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface LoanApprovalMapper {

    @Mapping(target = "approverName", ignore = true)
    LoanApprovalResponse toResponse(LoanApproval loanApproval);
}
