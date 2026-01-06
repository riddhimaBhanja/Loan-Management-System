package com.loanmanagement.emi.application.mapper;

import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.domain.model.EmiSchedule;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for EmiSchedule entity
 */
@Mapper(componentModel = "spring")
public interface EmiScheduleMapper {

    /**
     * Map EmiSchedule entity to EmiScheduleResponse DTO
     */
    EmiScheduleResponse toResponse(EmiSchedule emiSchedule);

    /**
     * Map list of EmiSchedule entities to list of EmiScheduleResponse DTOs
     */
    List<EmiScheduleResponse> toResponseList(List<EmiSchedule> emiSchedules);
}
