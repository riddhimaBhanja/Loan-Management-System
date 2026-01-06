package com.loanmanagement.emi.application.mapper;

import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmiScheduleMapperTest {

    private final EmiScheduleMapper mapper =
            Mappers.getMapper(EmiScheduleMapper.class);

    @Test
    void shouldMapEmiScheduleToResponse() {
        LocalDate dueDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        EmiSchedule emiSchedule = EmiSchedule.builder()
                .id(1L)
                .loanId(100L)
                .customerId(10L)
                .emiNumber(1)
                .emiAmount(BigDecimal.valueOf(8500))
                .principalComponent(BigDecimal.valueOf(7000))
                .interestComponent(BigDecimal.valueOf(1500))
                .dueDate(dueDate)
                .outstandingBalance(BigDecimal.valueOf(93000))
                .status(EmiStatus.PAID)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        EmiScheduleResponse response = mapper.toResponse(emiSchedule);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getLoanId());
        assertEquals(10L, response.getCustomerId());
        assertEquals(1, response.getEmiNumber());
        assertEquals(BigDecimal.valueOf(8500), response.getEmiAmount());
        assertEquals(BigDecimal.valueOf(7000), response.getPrincipalComponent());
        assertEquals(BigDecimal.valueOf(1500), response.getInterestComponent());
        assertEquals(dueDate, response.getDueDate());
        assertEquals(BigDecimal.valueOf(93000), response.getOutstandingBalance());
        assertEquals(EmiStatus.PAID, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void shouldMapEmiScheduleListToResponseList() {
        EmiSchedule emi1 = EmiSchedule.builder()
                .id(1L)
                .emiNumber(1)
                .emiAmount(BigDecimal.valueOf(8000))
                .status(EmiStatus.PENDING)
                .build();

        EmiSchedule emi2 = EmiSchedule.builder()
                .id(2L)
                .emiNumber(2)
                .emiAmount(BigDecimal.valueOf(8000))
                .status(EmiStatus.OVERDUE)
                .build();

        List<EmiScheduleResponse> responses =
                mapper.toResponseList(List.of(emi1, emi2));

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals(1, responses.get(0).getEmiNumber());
        assertEquals(BigDecimal.valueOf(8000), responses.get(0).getEmiAmount());
        assertEquals(EmiStatus.PENDING, responses.get(0).getStatus());

        assertEquals(2L, responses.get(1).getId());
        assertEquals(2, responses.get(1).getEmiNumber());
        assertEquals(BigDecimal.valueOf(8000), responses.get(1).getEmiAmount());
        assertEquals(EmiStatus.OVERDUE, responses.get(1).getStatus());
    }
}
