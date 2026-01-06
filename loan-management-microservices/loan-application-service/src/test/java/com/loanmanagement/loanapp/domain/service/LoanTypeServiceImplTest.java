package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.application.mapper.LoanTypeMapper;
import com.loanmanagement.loanapp.domain.model.LoanType;
import com.loanmanagement.loanapp.domain.repository.LoanTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanTypeServiceImplTest {

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private LoanTypeMapper loanTypeMapper;

    @InjectMocks
    private LoanTypeServiceImpl loanTypeService;

    private CreateLoanTypeRequest createValidRequest() {
        CreateLoanTypeRequest request = new CreateLoanTypeRequest();
        request.setName("Home Loan");
        request.setMinAmount(BigDecimal.valueOf(10000));
        request.setMaxAmount(BigDecimal.valueOf(500000));
        request.setMinTenureMonths(6);
        request.setMaxTenureMonths(240);
        request.setInterestRate(BigDecimal.valueOf(8.5));
        return request;
    }

    private LoanType createLoanType(Long id) {
        LoanType loanType = new LoanType();
        loanType.setId(id);
        loanType.setName("Home Loan");
        loanType.setIsActive(true);
        return loanType;
    }

    @Test
    void createLoanType_success() {
        CreateLoanTypeRequest request = createValidRequest();
        LoanType loanType = createLoanType(1L);

        when(loanTypeRepository.existsByName("Home Loan")).thenReturn(false);
        when(loanTypeMapper.toEntity(request)).thenReturn(loanType);
        when(loanTypeRepository.save(any())).thenReturn(loanType);
        when(loanTypeMapper.toResponse(loanType)).thenReturn(new LoanTypeResponse());

        LoanTypeResponse response = loanTypeService.createLoanType(request);

        assertNotNull(response);
    }

    @Test
    void createLoanType_shouldFail_whenNameExists() {
        CreateLoanTypeRequest request = createValidRequest();

        when(loanTypeRepository.existsByName("Home Loan")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> loanTypeService.createLoanType(request));
    }

    @Test
    void createLoanType_shouldFail_whenInvalidAmountRange() {
        CreateLoanTypeRequest request = createValidRequest();
        request.setMinAmount(BigDecimal.valueOf(500000));
        request.setMaxAmount(BigDecimal.valueOf(10000));

        when(loanTypeRepository.existsByName("Home Loan")).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> loanTypeService.createLoanType(request));
    }

    @Test
    void getLoanTypeById_success() {
        LoanType loanType = createLoanType(1L);

        when(loanTypeRepository.findById(1L))
                .thenReturn(Optional.of(loanType));
        when(loanTypeMapper.toResponse(loanType))
                .thenReturn(new LoanTypeResponse());

        LoanTypeResponse response = loanTypeService.getLoanTypeById(1L);

        assertNotNull(response);
    }

    @Test
    void getLoanTypeById_shouldFail_whenNotFound() {
        when(loanTypeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanTypeService.getLoanTypeById(1L));
    }

    @Test
    void getAllLoanTypes_success() {
        when(loanTypeRepository.findAll())
                .thenReturn(List.of(createLoanType(1L)));
        when(loanTypeMapper.toResponse(any()))
                .thenReturn(new LoanTypeResponse());

        List<LoanTypeResponse> responses = loanTypeService.getAllLoanTypes();

        assertEquals(1, responses.size());
    }

    @Test
    void getActiveLoanTypes_success() {
        when(loanTypeRepository.findByIsActiveTrue())
                .thenReturn(List.of(createLoanType(1L)));
        when(loanTypeMapper.toResponse(any()))
                .thenReturn(new LoanTypeResponse());

        List<LoanTypeResponse> responses = loanTypeService.getActiveLoanTypes();

        assertEquals(1, responses.size());
    }

    @Test
    void updateLoanType_success() {
        UpdateLoanTypeRequest request = new UpdateLoanTypeRequest();
        LoanType loanType = createLoanType(1L);

        when(loanTypeRepository.findById(1L))
                .thenReturn(Optional.of(loanType));
        when(loanTypeRepository.save(any()))
                .thenReturn(loanType);
        when(loanTypeMapper.toResponse(loanType))
                .thenReturn(new LoanTypeResponse());

        LoanTypeResponse response =
                loanTypeService.updateLoanType(1L, request);

        assertNotNull(response);
    }

    @Test
    void updateLoanType_shouldFail_whenInvalidTenureRange() {
        UpdateLoanTypeRequest request = new UpdateLoanTypeRequest();
        request.setMinTenureMonths(24);
        request.setMaxTenureMonths(6);

        when(loanTypeRepository.findById(1L))
                .thenReturn(Optional.of(createLoanType(1L)));

        assertThrows(BusinessException.class,
                () -> loanTypeService.updateLoanType(1L, request));
    }

    @Test
    void deleteLoanType_success() {
        LoanType loanType = createLoanType(1L);

        when(loanTypeRepository.findById(1L))
                .thenReturn(Optional.of(loanType));
        when(loanTypeRepository.save(any()))
                .thenReturn(loanType);

        loanTypeService.deleteLoanType(1L);

        assertFalse(loanType.getIsActive());
    }
}
