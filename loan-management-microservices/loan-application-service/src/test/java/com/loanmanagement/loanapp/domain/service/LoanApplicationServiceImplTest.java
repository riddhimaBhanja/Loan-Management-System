package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.application.dto.request.AssignOfficerRequest;
import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.application.mapper.LoanMapper;
import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.model.Loan;
import com.loanmanagement.loanapp.domain.model.LoanType;
import com.loanmanagement.loanapp.domain.repository.LoanRepository;
import com.loanmanagement.loanapp.domain.repository.LoanTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanApplicationServiceImpl loanApplicationService;

    private Loan createLoan(Long id) {
        Loan loan = new Loan();
        loan.setId(id);
        loan.setCustomerId(1L);
        loan.setLoanTypeId(10L);
        loan.setAmount(BigDecimal.valueOf(50000));
        loan.setTenureMonths(12);
        loan.setEmploymentStatus(EmploymentStatus.SALARIED);
        loan.setMonthlyIncome(BigDecimal.valueOf(40000));
        loan.setStatus(LoanStatus.PENDING);
        loan.setAppliedDate(LocalDateTime.now());
        return loan;
    }

    private LoanType createLoanType(boolean active) {
        LoanType loanType = new LoanType();
        loanType.setId(10L);
        loanType.setIsActive(active);
        loanType.setMinAmount(BigDecimal.valueOf(10000));
        loanType.setMaxAmount(BigDecimal.valueOf(100000));
        loanType.setMinTenureMonths(6);
        loanType.setMaxTenureMonths(60);
        return loanType;
    }

    @Test
    void createLoanApplication_success() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanTypeId(10L);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setTenureMonths(12);

        LoanType loanType = createLoanType(true);
        Loan loan = createLoan(1L);

        when(loanTypeRepository.findById(10L)).thenReturn(Optional.of(loanType));
        when(loanMapper.toEntity(any(), any())).thenReturn(loan);
        when(loanRepository.save(any())).thenReturn(loan);
        when(loanMapper.toResponse(any())).thenReturn(new LoanResponse());

        LoanResponse response =
                loanApplicationService.createLoanApplication(request, 1L);

        assertNotNull(response);
    }

    @Test
    void createLoanApplication_shouldFail_whenLoanTypeInactive() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanTypeId(10L);

        when(loanTypeRepository.findById(10L))
                .thenReturn(Optional.of(createLoanType(false)));

        assertThrows(BusinessException.class,
                () -> loanApplicationService.createLoanApplication(request, 1L));
    }

    @Test
    void getLoanById_success() {
        Loan loan = createLoan(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toResponse(any())).thenReturn(new LoanResponse());

        LoanResponse response = loanApplicationService.getLoanById(1L);

        assertNotNull(response);
    }

    @Test
    void getLoanById_shouldThrowException_whenNotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanApplicationService.getLoanById(1L));
    }

    @Test
    void getCustomerLoans_success() {
        when(loanRepository.findByCustomerId(1L))
                .thenReturn(List.of(createLoan(1L)));
        when(loanMapper.toResponse(any())).thenReturn(new LoanResponse());

        List<LoanResponse> responses =
                loanApplicationService.getCustomerLoans(1L);

        assertEquals(1, responses.size());
    }

    @Test
    void getLoansByStatus_success() {
        when(loanRepository.findByStatus(LoanStatus.APPROVED))
                .thenReturn(List.of(createLoan(1L)));
        when(loanMapper.toResponse(any())).thenReturn(new LoanResponse());

        List<LoanResponse> responses =
                loanApplicationService.getLoansByStatus("APPROVED");

        assertEquals(1, responses.size());
    }

    @Test
    void getLoansByStatus_shouldFail_forInvalidStatus() {
        assertThrows(BusinessException.class,
                () -> loanApplicationService.getLoansByStatus("INVALID"));
    }

    @Test
    void assignOfficer_success() {
        Loan loan = createLoan(1L);
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setLoanOfficerId(99L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);
        when(loanMapper.toResponse(any())).thenReturn(new LoanResponse());

        LoanResponse response =
                loanApplicationService.assignOfficer(1L, request);

        assertNotNull(response);
        assertEquals(99L, loan.getLoanOfficerId());
    }

    @Test
    void unassignOfficer_success() {
        Loan loan = createLoan(1L);
        loan.setLoanOfficerId(50L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);
        when(loanMapper.toResponse(any())).thenReturn(new LoanResponse());

        LoanResponse response =
                loanApplicationService.unassignOfficer(1L);

        assertNotNull(response);
        assertNull(loan.getLoanOfficerId());
    }

    @Test
    void approveLoan_shouldUpdateStatus() {
        Loan loan = createLoan(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        loanApplicationService.approveLoan(1L, BigDecimal.valueOf(40000), BigDecimal.valueOf(9));

        assertEquals(LoanStatus.APPROVED, loan.getStatus());
    }

    @Test
    void rejectLoan_shouldUpdateStatus() {
        Loan loan = createLoan(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        loanApplicationService.rejectLoan(1L, "Rejected");

        assertEquals(LoanStatus.REJECTED, loan.getStatus());
    }

    @Test
    void disburseLoan_shouldUpdateStatus() {
        Loan loan = createLoan(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        loanApplicationService.disburseLoan(1L, null, null, null);

        assertEquals(LoanStatus.DISBURSED, loan.getStatus());
    }

    @Test
    void closeLoan_shouldUpdateStatus() {
        Loan loan = createLoan(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        loanApplicationService.closeLoan(1L);

        assertEquals(LoanStatus.CLOSED, loan.getStatus());
    }
}
