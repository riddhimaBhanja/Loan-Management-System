package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Loan Application operations
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse> createLoanApplication(
            @Valid @RequestBody LoanApplicationRequest request,
            Authentication authentication) {
        // Extract user ID from UserPrincipal
        com.loanmanagement.loanapp.infrastructure.security.UserPrincipal userPrincipal =
                (com.loanmanagement.loanapp.infrastructure.security.UserPrincipal) authentication.getPrincipal();
        Long customerId = userPrincipal.getUserId();
        log.info("Creating loan application for customer ID: {}", customerId);

        LoanResponse response = loanApplicationService.createLoanApplication(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.LOAN_APPLICATION_CREATED, response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getLoanById(@PathVariable Long id) {
        log.info("Fetching loan with ID: {}", id);
        LoanResponse response = loanApplicationService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOAN_FETCHED, response));
    }

    @GetMapping("/my-loans")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getMyLoans(Authentication authentication) {
        com.loanmanagement.loanapp.infrastructure.security.UserPrincipal userPrincipal =
                (com.loanmanagement.loanapp.infrastructure.security.UserPrincipal) authentication.getPrincipal();
        Long customerId = userPrincipal.getUserId();
        log.info("Fetching loans for customer ID: {}", customerId);
        List<LoanResponse> responses = loanApplicationService.getCustomerLoans(customerId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOANS_FETCHED, responses));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getCustomerLoans(@PathVariable Long customerId) {
        log.info("Fetching loans for customer ID: {}", customerId);
        List<LoanResponse> responses = loanApplicationService.getCustomerLoans(customerId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOANS_FETCHED, responses));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getAllLoans(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Fetching loans - status: {}, page: {}, size: {}", status, page, size);

        List<LoanResponse> responses;
        if (status != null && !status.isEmpty()) {
            responses = loanApplicationService.getLoansByStatus(status);
        } else {
            responses = loanApplicationService.getAllLoans();
        }

        // Note: Pagination parameters (page, size) are currently ignored
        // Full pagination support can be added later if needed
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOANS_FETCHED, responses));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getLoansByStatus(@PathVariable String status) {
        log.info("Fetching loans with status: {}", status);
        List<LoanResponse> responses = loanApplicationService.getLoansByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOANS_FETCHED, responses));
    }
}
