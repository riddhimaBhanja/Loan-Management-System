package com.loanmanagement.emi.domain.service;

import com.loanmanagement.common.dto.GenerateEmiRequest;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.application.dto.response.EmiSummaryResponse;
import com.loanmanagement.emi.application.mapper.EmiScheduleMapper;
import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import com.loanmanagement.emi.domain.repository.EmiScheduleRepository;
import com.loanmanagement.emi.shared.constants.MessageConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmiScheduleServiceImplTest {

    @InjectMocks
    private EmiScheduleServiceImpl service;

    @Mock
    private EmiScheduleRepository emiScheduleRepository;

    @Mock
    private EmiCalculationService emiCalculationService;

    @Mock
    private EmiScheduleMapper emiScheduleMapper;

    private EmiSchedule emi1;
    private EmiSchedule emi2;

    @BeforeEach
    void setUp() {
        emi1 = EmiSchedule.builder()
                .id(1L)
                .loanId(100L)
                .customerId(10L)
                .emiNumber(1)
                .emiAmount(new BigDecimal("5000"))
                .dueDate(LocalDate.now().plusDays(5))
                .status(EmiStatus.PENDING)
                .build();

        emi2 = EmiSchedule.builder()
                .id(2L)
                .loanId(100L)
                .customerId(10L)
                .emiNumber(2)
                .emiAmount(new BigDecimal("5000"))
                .dueDate(LocalDate.now().plusDays(25))
                .status(EmiStatus.PAID)
                .build();
    }

    @Test
    void generateEmiSchedule_success() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(100L)
                .customerId(10L)
                .principal(new BigDecimal("10000"))
                .interestRate(new BigDecimal("10"))
                .tenureMonths(2)
                .startDate(LocalDate.now())
                .build();

        when(emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(100L))
                .thenReturn(List.of());
        when(emiCalculationService.generateEmiSchedule(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(emi1, emi2));
        when(emiScheduleRepository.saveAll(any()))
                .thenReturn(List.of(emi1, emi2));
        when(emiScheduleMapper.toResponseList(any()))
                .thenReturn(List.of(new EmiScheduleResponse(), new EmiScheduleResponse()));

        List<EmiScheduleResponse> responses = service.generateEmiSchedule(request);

        assertEquals(2, responses.size());
    }

    @Test
    void generateEmiSchedule_alreadyExists() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(100L)
                .build();

        when(emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(100L))
                .thenReturn(List.of(emi1));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.generateEmiSchedule(request)
        );

        assertEquals(MessageConstants.EMI_ALREADY_EXISTS, ex.getMessage());
    }

    @Test
    void getEmiSchedule_success() {
        when(emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(100L))
                .thenReturn(List.of(emi1, emi2));
        when(emiScheduleMapper.toResponseList(any()))
                .thenReturn(List.of(new EmiScheduleResponse(), new EmiScheduleResponse()));

        List<EmiScheduleResponse> responses = service.getEmiSchedule(100L);

        assertEquals(2, responses.size());
    }

    @Test
    void getEmiSchedule_notFound() {
        when(emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(100L))
                .thenReturn(List.of());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getEmiSchedule(100L)
        );

        assertEquals(MessageConstants.EMI_SCHEDULE_NOT_FOUND, ex.getMessage());
    }

    @Test
    void getEmiScheduleByCustomer_success() {
        when(emiScheduleRepository.findByCustomerIdOrderByDueDateAsc(10L))
                .thenReturn(List.of(emi1));
        when(emiScheduleMapper.toResponseList(any()))
                .thenReturn(List.of(new EmiScheduleResponse()));

        List<EmiScheduleResponse> responses = service.getEmiScheduleByCustomer(10L);

        assertEquals(1, responses.size());
    }

    @Test
    void getEmiSummary_success() {
        when(emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(100L))
                .thenReturn(List.of(emi1, emi2));
        when(emiScheduleRepository.getTotalOutstandingAmount(100L))
                .thenReturn(Optional.of(new BigDecimal("5000")));

        EmiSummaryResponse summary = service.getEmiSummary(100L);

        assertEquals(2, summary.getTotalEmis());
        assertEquals(1, summary.getPaidEmis());
        assertEquals(1, summary.getPendingEmis());
    }

    @Test
    void getEmiSummary_notFound() {
        when(emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(100L))
                .thenReturn(List.of());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getEmiSummary(100L)
        );

        assertEquals(MessageConstants.EMI_SCHEDULE_NOT_FOUND, ex.getMessage());
    }

    @Test
    void getOverdueEmis_success() {
        when(emiScheduleRepository.findOverdueEmis(any()))
                .thenReturn(List.of(emi1));
        when(emiScheduleMapper.toResponseList(any()))
                .thenReturn(List.of(new EmiScheduleResponse()));

        List<EmiScheduleResponse> responses = service.getOverdueEmis();

        assertEquals(1, responses.size());
    }

    @Test
    void markOverdueEmis_success() {
        when(emiScheduleRepository.findOverdueEmis(any()))
                .thenReturn(List.of(emi1));

        int count = service.markOverdueEmis();

        assertEquals(1, count);
        verify(emiScheduleRepository).saveAll(any());
    }

    @Test
    void verifyAllEmisPaid_success() {
        when(emiScheduleRepository.areAllEmisPaid(100L)).thenReturn(true);

        boolean result = service.verifyAllEmisPaid(100L);

        assertTrue(result);
    }

    @Test
    void getOutstandingAmount_success() {
        when(emiScheduleRepository.getTotalOutstandingAmount(100L))
                .thenReturn(Optional.of(new BigDecimal("5000")));

        BigDecimal outstanding = service.getOutstandingAmount(100L);

        assertEquals(new BigDecimal("5000"), outstanding);
    }
}
