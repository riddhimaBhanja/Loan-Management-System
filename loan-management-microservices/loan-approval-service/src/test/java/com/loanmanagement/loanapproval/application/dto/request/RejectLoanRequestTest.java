package com.loanmanagement.loanapproval.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RejectLoanRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRejectLoanRequest_shouldPassValidation() {
        RejectLoanRequest request = RejectLoanRequest.builder()
                .rejectionReason("Income does not meet eligibility criteria")
                .notes("Applicant can reapply after 6 months")
                .build();

        Set<ConstraintViolation<RejectLoanRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankRejectionReason_shouldFailValidation() {
        RejectLoanRequest request = RejectLoanRequest.builder()
                .rejectionReason(" ")
                .build();

        Set<ConstraintViolation<RejectLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectionReasonTooShort_shouldFailValidation() {
        RejectLoanRequest request = RejectLoanRequest.builder()
                .rejectionReason("Too low")
                .build();

        Set<ConstraintViolation<RejectLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectionReasonTooLong_shouldFailValidation() {
        RejectLoanRequest request = RejectLoanRequest.builder()
                .rejectionReason("R".repeat(1001))
                .build();

        Set<ConstraintViolation<RejectLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void notesExceedingMaxLength_shouldFailValidation() {
        RejectLoanRequest request = RejectLoanRequest.builder()
                .rejectionReason("Income does not meet eligibility criteria")
                .notes("N".repeat(501))
                .build();

        Set<ConstraintViolation<RejectLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
