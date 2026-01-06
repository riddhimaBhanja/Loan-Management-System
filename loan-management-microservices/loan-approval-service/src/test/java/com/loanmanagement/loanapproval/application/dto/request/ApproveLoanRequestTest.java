package com.loanmanagement.loanapproval.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApproveLoanRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validApproveLoanRequest_shouldPassValidation() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("50000.00"))
                .interestRate(new BigDecimal("10.50"))
                .notes("Approved as per eligibility")
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullApprovedAmount_shouldFailValidation() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(null)
                .interestRate(new BigDecimal("10.00"))
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void approvedAmountLessThanMinimum_shouldFailValidation() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("500.00"))
                .interestRate(new BigDecimal("10.00"))
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void nullInterestRate_shouldFailValidation() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("10000.00"))
                .interestRate(null)
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void interestRateBelowMinimum_shouldFailValidation() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("0.00"))
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void interestRateAboveMaximum_shouldFailValidation() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("60.00"))
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void notesExceedingMaxLength_shouldFailValidation() {
        String longNotes = "a".repeat(1001);

        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("12.00"))
                .notes(longNotes)
                .build();

        Set<ConstraintViolation<ApproveLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
