package com.loanmanagement.emi.application.mapper;

import com.loanmanagement.emi.application.dto.response.EmiPaymentResponse;
import com.loanmanagement.emi.domain.model.EmiPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for EmiPayment entity
 */
@Mapper(componentModel = "spring")
public interface EmiPaymentMapper {

    /**
     * Map EmiPayment entity to EmiPaymentResponse DTO
     */
    @Mapping(target = "emiNumber", ignore = true)
    @Mapping(target = "paidByName", ignore = true)
    EmiPaymentResponse toResponse(EmiPayment emiPayment);

    /**
     * Map list of EmiPayment entities to list of EmiPaymentResponse DTOs
     */
    List<EmiPaymentResponse> toResponseList(List<EmiPayment> emiPayments);
}
