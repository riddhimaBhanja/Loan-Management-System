package com.loanmanagement.loanapp.application.mapper;

import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.domain.model.LoanType;
import org.mapstruct.*;

/**
 * MapStruct mapper for LoanType entity
 */
@Mapper(componentModel = "spring")
public interface LoanTypeMapper {

    LoanTypeResponse toResponse(LoanType loanType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LoanType toEntity(CreateLoanTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateLoanTypeRequest request, @MappingTarget LoanType loanType);
}
