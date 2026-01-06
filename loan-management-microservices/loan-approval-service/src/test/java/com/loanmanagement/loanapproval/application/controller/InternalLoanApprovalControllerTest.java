package com.loanmanagement.loanapproval.application.controller;

import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.domain.model.LoanApproval.ApprovalStatus;
import com.loanmanagement.loanapproval.domain.service.LoanApprovalService;
import com.loanmanagement.loanapproval.domain.service.LoanDisbursementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalLoanApprovalControllerTest {

    @Mock
    private LoanApprovalService loanApprovalService;

    @Mock
    private LoanDisbursementService loanDisbursementService;

    @InjectMocks
    private InternalLoanApprovalController controller;

    @Test
    void getApprovalByLoanId_success() {
        Long loanId = 1L;
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .loanId(loanId)
                .status(ApprovalStatus.APPROVED)
                .build();

        when(loanApprovalService.getApprovalByLoanId(loanId)).thenReturn(response);

        ResponseEntity<LoanApprovalResponse> result = controller.getApprovalByLoanId(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(loanApprovalService).getApprovalByLoanId(loanId);
    }

    @Test
    void getDisbursementByLoanId_success() {
        Long loanId = 2L;
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .loanId(loanId)
                .build();

        when(loanDisbursementService.getDisbursementByLoanId(loanId)).thenReturn(response);

        ResponseEntity<LoanDisbursementResponse> result = controller.getDisbursementByLoanId(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(loanDisbursementService).getDisbursementByLoanId(loanId);
    }

    @Test
    void isLoanApproved_true() {
        Long loanId = 3L;
        LoanApprovalResponse response = LoanApprovalResponse.builder()
                .loanId(loanId)
                .status(ApprovalStatus.APPROVED)
                .build();

        when(loanApprovalService.getApprovalByLoanId(loanId)).thenReturn(response);

        ResponseEntity<Boolean> result = controller.isLoanApproved(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
    }

    @Test
    void isLoanApproved_false_whenException() {
        Long loanId = 4L;

        when(loanApprovalService.getApprovalByLoanId(loanId))
                .thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Boolean> result = controller.isLoanApproved(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody());
    }

    @Test
    void isLoanDisbursed_true() {
        Long loanId = 5L;
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .loanId(loanId)
                .build();

        when(loanDisbursementService.getDisbursementByLoanId(loanId)).thenReturn(response);

        ResponseEntity<Boolean> result = controller.isLoanDisbursed(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
    }

    @Test
    void isLoanDisbursed_false_whenException() {
        Long loanId = 6L;

        when(loanDisbursementService.getDisbursementByLoanId(loanId))
                .thenThrow(new RuntimeException("Not disbursed"));

        ResponseEntity<Boolean> result = controller.isLoanDisbursed(loanId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody());
    }
}
