package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapproval.application.dto.request.ApproveLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.RejectLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.application.mapper.LoanApprovalMapper;
import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import com.loanmanagement.loanapproval.domain.repository.LoanApprovalRepository;
import com.loanmanagement.loanapproval.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.NotificationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
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
class LoanApprovalServiceImplTest {

    @Mock
    private LoanApprovalRepository loanApprovalRepository;

    @Mock
    private LoanApprovalMapper loanApprovalMapper;

    @Mock
    private LoanApplicationServiceClient loanApplicationServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private LoanApprovalServiceImpl loanApprovalService;

    private LoanDTO loanDTO;
    private UserDetailsDTO userDetailsDTO;
    private LoanApproval loanApproval;
    private LoanApprovalResponse loanApprovalResponse;

    @BeforeEach
    void setUp() {
        loanDTO = new LoanDTO();
        loanDTO.setId(1L);
        loanDTO.setCustomerId(10L);
        loanDTO.setRequestedAmount(new BigDecimal("100000"));
        loanDTO.setStatus("PENDING");

        userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setFirstName("John");
        userDetailsDTO.setLastName("Doe");
        userDetailsDTO.setEmail("john.doe@test.com");

        loanApproval = LoanApproval.builder()
                .loanId(1L)
                .approverId(100L)
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .approvedAmount(new BigDecimal("90000"))
                .interestRate(new BigDecimal("8.5"))
                .decisionDate(LocalDateTime.now())
                .build();

        loanApprovalResponse = new LoanApprovalResponse();
    }

    @Test
    void approveLoan_success() {
        ApproveLoanRequest request = new ApproveLoanRequest();
        request.setApprovedAmount(new BigDecimal("90000"));
        request.setInterestRate(new BigDecimal("8.5"));
        request.setNotes("Approved");

        when(userServiceClient.userHasAnyRole(100L, List.of("LOAN_OFFICER", "ADMIN"))).thenReturn(true);
        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);
        when(loanApprovalRepository.findByLoanId(1L)).thenReturn(Optional.empty());
        when(loanApprovalRepository.save(any(LoanApproval.class))).thenReturn(loanApproval);
        when(loanApprovalMapper.toResponse(any(LoanApproval.class))).thenReturn(loanApprovalResponse);
        when(userServiceClient.getUserById(10L)).thenReturn(userDetailsDTO);
        when(userServiceClient.getUserById(100L)).thenReturn(userDetailsDTO);

        LoanApprovalResponse response = loanApprovalService.approveLoan(1L, request, 100L);

        assertNotNull(response);
        verify(loanApplicationServiceClient).updateLoanStatusToApproved(1L, request.getApprovedAmount(), request.getInterestRate());
        verify(notificationServiceClient).sendLoanApprovedNotification(
                eq("john.doe@test.com"),
                eq("John Doe"),
                eq("1"),
                eq("100000"),
                eq("90000")
        );
    }

    @Test
    void approveLoan_unauthorized() {
        ApproveLoanRequest request = new ApproveLoanRequest();
        request.setApprovedAmount(new BigDecimal("90000"));

        when(userServiceClient.userHasAnyRole(100L, List.of("LOAN_OFFICER", "ADMIN"))).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> loanApprovalService.approveLoan(1L, request, 100L));
    }

    @Test
    void rejectLoan_success() {
        RejectLoanRequest request = new RejectLoanRequest();
        request.setRejectionReason("Low credit score");
        request.setNotes("Rejected");

        when(userServiceClient.userHasAnyRole(100L, List.of("LOAN_OFFICER", "ADMIN"))).thenReturn(true);
        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);
        when(loanApprovalRepository.findByLoanId(1L)).thenReturn(Optional.empty());
        when(loanApprovalRepository.save(any(LoanApproval.class))).thenReturn(loanApproval);
        when(loanApprovalMapper.toResponse(any(LoanApproval.class))).thenReturn(loanApprovalResponse);
        when(userServiceClient.getUserById(10L)).thenReturn(userDetailsDTO);
        when(userServiceClient.getUserById(100L)).thenReturn(userDetailsDTO);

        LoanApprovalResponse response = loanApprovalService.rejectLoan(1L, request, 100L);

        assertNotNull(response);
        verify(loanApplicationServiceClient).updateLoanStatusToRejected(1L, request.getRejectionReason());
        verify(notificationServiceClient).sendLoanRejectedNotification(
                eq("john.doe@test.com"),
                eq("John Doe"),
                eq("1"),
                eq("Low credit score")
        );
    }

    @Test
    void getLoanForReview_success() {
        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);

        LoanDTO result = loanApprovalService.getLoanForReview(1L);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void getApprovalByLoanId_success() {
        when(loanApprovalRepository.findByLoanId(1L)).thenReturn(Optional.of(loanApproval));
        when(loanApprovalMapper.toResponse(loanApproval)).thenReturn(loanApprovalResponse);
        when(userServiceClient.getUserById(100L)).thenReturn(userDetailsDTO);

        LoanApprovalResponse response = loanApprovalService.getApprovalByLoanId(1L);

        assertNotNull(response);
    }

    @Test
    void getApprovalByLoanId_notFound() {
        when(loanApprovalRepository.findByLoanId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanApprovalService.getApprovalByLoanId(1L));
    }

    @Test
    void getApprovedLoansCount_success() {
        when(loanApprovalRepository.countByStatus(LoanApproval.ApprovalStatus.APPROVED)).thenReturn(5L);

        Long count = loanApprovalService.getApprovedLoansCount();

        assertEquals(5L, count);
    }

    @Test
    void getRejectedLoansCount_success() {
        when(loanApprovalRepository.countByStatus(LoanApproval.ApprovalStatus.REJECTED)).thenReturn(3L);

        Long count = loanApprovalService.getRejectedLoansCount();

        assertEquals(3L, count);
    }
}
