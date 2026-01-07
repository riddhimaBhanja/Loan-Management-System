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
    @Test
    void noArgsConstructorAndSetters_shouldWorkCorrectly() {
        DisburseLoanRequest request = new DisburseLoanRequest();

        LocalDate date = LocalDate.now();
        request.setDisbursementDate(date);
        request.setDisbursementMethod("NEFT");
        request.setReferenceNumber("REF-001");
        request.setRemarks("Test remarks");

        assertEquals(date, request.getDisbursementDate());
        assertEquals("NEFT", request.getDisbursementMethod());
        assertEquals("REF-001", request.getReferenceNumber());
        assertEquals("Test remarks", request.getRemarks());
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        LocalDate date = LocalDate.now();

        DisburseLoanRequest request = new DisburseLoanRequest(
                date,
                "BANK_TRANSFER",
                "TXN-999",
                "All args constructor"
        );

        assertEquals(date, request.getDisbursementDate());
        assertEquals("BANK_TRANSFER", request.getDisbursementMethod());
        assertEquals("TXN-999", request.getReferenceNumber());
        assertEquals("All args constructor", request.getRemarks());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LocalDate date = LocalDate.now();

        DisburseLoanRequest r1 = DisburseLoanRequest.builder()
                .disbursementDate(date)
                .disbursementMethod("NEFT")
                .referenceNumber("REF-123")
                .remarks("OK")
                .build();

        DisburseLoanRequest r2 = DisburseLoanRequest.builder()
                .disbursementDate(date)
                .disbursementMethod("NEFT")
                .referenceNumber("REF-123")
                .remarks("OK")
                .build();

        DisburseLoanRequest r3 = DisburseLoanRequest.builder()
                .disbursementDate(date)
                .disbursementMethod("CHEQUE")
                .referenceNumber("REF-999")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);

        // Lombok canEqual path
        assertTrue(r1.canEqual(r2));
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "not-a-disburse-loan-request");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementMethod("NEFT")
                .referenceNumber("REF-777")
                .build();

        String value = request.toString();

        assertNotNull(value);
        assertTrue(value.contains("DisburseLoanRequest"));
        assertTrue(value.contains("NEFT"));
        assertTrue(value.contains("REF-777"));
    }

    @Test
    void canEqual_shouldReturnFalseForDifferentClass() {
        DisburseLoanRequest request = DisburseLoanRequest.builder()
                .disbursementDate(LocalDate.now())
                .build();

        assertFalse(request.canEqual("invalid-object"));
    }

}
