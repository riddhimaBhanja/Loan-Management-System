package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.domain.service.LoanTypeService;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Loan Type operations
 */
@RestController
@RequestMapping("/api/loan-types")
@RequiredArgsConstructor
@Slf4j
public class LoanTypeController {

    private final LoanTypeService loanTypeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createLoanType(
            @Valid @RequestBody CreateLoanTypeRequest request) {
        log.info("Creating loan type: {}", request.getName());
        LoanTypeResponse response = loanTypeService.createLoanType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.LOAN_TYPE_CREATED, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLoanTypeById(@PathVariable Long id) {
        log.info("Fetching loan type with ID: {}", id);
        LoanTypeResponse response = loanTypeService.getLoanTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOAN_TYPE_FETCHED, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllLoanTypes() {
        log.info("Fetching all loan types");
        List<LoanTypeResponse> responses = loanTypeService.getAllLoanTypes();
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOAN_TYPES_FETCHED, responses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveLoanTypes() {
        log.info("Fetching active loan types");
        List<LoanTypeResponse> responses = loanTypeService.getActiveLoanTypes();
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOAN_TYPES_FETCHED, responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateLoanType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLoanTypeRequest request) {
        log.info("Updating loan type with ID: {}", id);
        LoanTypeResponse response = loanTypeService.updateLoanType(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOAN_TYPE_UPDATED, response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteLoanType(@PathVariable Long id) {
        log.info("Deleting loan type with ID: {}", id);
        loanTypeService.deleteLoanType(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOAN_TYPE_DELETED, null));
    }
}
