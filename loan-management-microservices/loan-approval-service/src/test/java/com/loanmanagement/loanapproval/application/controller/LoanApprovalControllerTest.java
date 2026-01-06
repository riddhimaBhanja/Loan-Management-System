package com.loanmanagement.loanapproval.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.loanapproval.application.dto.request.ApproveLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.DisburseLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.RejectLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.domain.model.LoanApproval.ApprovalStatus;
import com.loanmanagement.loanapproval.domain.service.LoanApprovalService;
import com.loanmanagement.loanapproval.domain.service.LoanClosureService;
import com.loanmanagement.loanapproval.domain.service.LoanDisbursementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApprovalControllerTest {

    @Mock
    private LoanApprovalService loanApprovalService;

    @Mock
    private LoanDisbursementService loanDisbursementService;

    @Mock
    private LoanClosureService loanClosureService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoanApprovalController controller;

    @Test
    void approveLoan_success() {
        Long loanId = 1L;
        ApproveLoanRequest request = new ApproveLoanRequest();
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .loanId(loanId)
                .status(ApprovalStatus.APPROVED)
                .build();

        when(authentication.getName()).thenReturn("officer");
        when(loanApprovalService.approveLoan(eq(loanId), eq(request), anyLong()))
                .thenReturn(response);

        ResponseEntity<ApiResponse> result =
                controller.approveLoan(loanId, request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Loan approved successfully", result.getBody().getMessage());
    }

    @Test
    void rejectLoan_success() {
        Long loanId = 2L;
        RejectLoanRequest request = new RejectLoanRequest();
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .loanId(loanId)
                .status(ApprovalStatus.REJECTED)
                .build();

        when(authentication.getName()).thenReturn("admin");
        when(loanApprovalService.rejectLoan(eq(loanId), eq(request), anyLong()))
                .thenReturn(response);

        ResponseEntity<ApiResponse> result =
                controller.rejectLoan(loanId, request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Loan rejected successfully", result.getBody().getMessage());
    }

    @Test
    void disburseLoan_success() {
        Long loanId = 3L;
        DisburseLoanRequest request = new DisburseLoanRequest();
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .loanId(loanId)
                .build();

        when(authentication.getName()).thenReturn("officer");
        when(loanDisbursementService.disburseLoan(eq(loanId), eq(request), anyLong()))
                .thenReturn(response);

        ResponseEntity<ApiResponse> result =
                controller.disburseLoan(loanId, request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Loan disbursed successfully", result.getBody().getMessage());
    }

    @Test
    void closeLoan_success() {
        Long loanId = 4L;
        LoanDTO loanDTO = new LoanDTO();

        when(authentication.getName()).thenReturn("admin");
        when(loanClosureService.closeLoan(loanId)).thenReturn(loanDTO);

        ResponseEntity<ApiResponse> result =
                controller.closeLoan(loanId, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Loan closed successfully", result.getBody().getMessage());
    }

    @Test
    void getApprovalByLoanId_success() {
        Long loanId = 5L;
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .loanId(loanId)
                .status(ApprovalStatus.APPROVED)
                .build();

        when(loanApprovalService.getApprovalByLoanId(loanId)).thenReturn(response);

        ResponseEntity<ApiResponse> result =
                controller.getApprovalByLoanId(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Approval details retrieved successfully", result.getBody().getMessage());
    }

    @Test
    void getDisbursementByLoanId_success() {
        Long loanId = 6L;
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .loanId(loanId)
                .build();

        when(loanDisbursementService.getDisbursementByLoanId(loanId))
                .thenReturn(response);

        ResponseEntity<ApiResponse> result =
                controller.getDisbursementByLoanId(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Disbursement details retrieved successfully", result.getBody().getMessage());
    }
}
