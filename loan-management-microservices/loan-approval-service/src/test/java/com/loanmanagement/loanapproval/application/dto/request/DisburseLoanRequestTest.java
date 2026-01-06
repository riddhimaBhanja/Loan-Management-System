package com.loanmanagement.loanapproval.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DisburseLoanRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDisburseLoanRequest_shouldPassValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("TXN123456")
                .remarks("Disbursed successfully")
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullDisbursementDate_shouldFailValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(null)
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("TXN123456")
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void blankDisbursementMethod_shouldFailValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .disbursementMethod(" ")
                .referenceNumber("TXN123456")
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void disbursementMethodExceedsMaxLength_shouldFailValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .disbursementMethod("A".repeat(51))
                .referenceNumber("TXN123456")
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void blankReferenceNumber_shouldFailValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("")
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void referenceNumberExceedsMaxLength_shouldFailValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("R".repeat(101))
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void remarksExceedingMaxLength_shouldFailValidation() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("TXN123456")
                .remarks("X".repeat(501))
                .build();

        Set<ConstraintViolation<DisburseLoanRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
