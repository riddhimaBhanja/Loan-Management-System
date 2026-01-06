package com.loanmanagement.emi.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.emi.application.dto.request.EmiPaymentRequest;
import com.loanmanagement.emi.application.dto.response.EmiPaymentResponse;
import com.loanmanagement.emi.domain.service.EmiPaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for EMI Payment operations
 */
@RestController
@RequestMapping("/api/emis")
public class EmiPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(EmiPaymentController.class);

    @Autowired
    private EmiPaymentService emiPaymentService;

    /**
     * Record EMI payment
     */
    @PostMapping("/{emiScheduleId}/pay")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<EmiPaymentResponse> recordPayment(
            @PathVariable Long emiScheduleId,
            @Valid @RequestBody EmiPaymentRequest request) {
        logger.info("Recording payment for EMI schedule ID: {}", emiScheduleId);

        // Set EMI schedule ID from path variable
        request.setEmiScheduleId(emiScheduleId);

        EmiPaymentResponse response = emiPaymentService.recordPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Record EMI payment (alternate endpoint)
     * EMI schedule ID is provided in the request body
     */
    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> recordPaymentAlt(
            @Valid @RequestBody EmiPaymentRequest request) {
        logger.info("Recording payment for EMI schedule ID: {}", request.getEmiScheduleId());

        EmiPaymentResponse response = emiPaymentService.recordPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded successfully", response));
    }

    /**
     * Get payment history for a loan
     */
    @GetMapping("/loan/{loanId}/payments")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getPaymentHistory(@PathVariable Long loanId) {
        logger.info("Fetching payment history for loan ID: {}", loanId);

        List<EmiPaymentResponse> responses = emiPaymentService.getPaymentHistory(loanId);

        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved successfully", responses));
    }

    /**
     * Get payment details by ID
     */
    @GetMapping("/payments/{paymentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<EmiPaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        logger.info("Fetching payment by ID: {}", paymentId);

        EmiPaymentResponse response = emiPaymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by transaction reference
     */
    @GetMapping("/payments/by-reference/{transactionReference}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<EmiPaymentResponse> getPaymentByReference(
            @PathVariable String transactionReference) {
        logger.info("Fetching payment by transaction reference: {}", transactionReference);

        EmiPaymentResponse response = emiPaymentService.getPaymentByTransactionReference(transactionReference);

        return ResponseEntity.ok(response);
    }
}
