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
    @Test
    void noArgsConstructorAndSetters_shouldWorkCorrectly() {
        ApproveLoanRequest request = new ApproveLoanRequest();

        request.setApprovedAmount(new BigDecimal("25000.00"));
        request.setInterestRate(new BigDecimal("9.75"));
        request.setNotes("Setter based approval");

        assertEquals(0, new BigDecimal("25000.00").compareTo(request.getApprovedAmount()));
        assertEquals(0, new BigDecimal("9.75").compareTo(request.getInterestRate()));
        assertEquals("Setter based approval", request.getNotes());
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        ApproveLoanRequest request = new ApproveLoanRequest(
                new BigDecimal("40000.00"),
                new BigDecimal("11.25"),
                "Approved via all-args"
        );

        assertEquals(0, new BigDecimal("40000.00").compareTo(request.getApprovedAmount()));
        assertEquals(0, new BigDecimal("11.25").compareTo(request.getInterestRate()));
        assertEquals("Approved via all-args", request.getNotes());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        ApproveLoanRequest r1 = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("30000.00"))
                .interestRate(new BigDecimal("10.50"))
                .notes("OK")
                .build();

        ApproveLoanRequest r2 = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("30000.00"))
                .interestRate(new BigDecimal("10.50"))
                .notes("OK")
                .build();

        ApproveLoanRequest r3 = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("60000.00"))
                .interestRate(new BigDecimal("12.00"))
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);

        // Lombok canEqual path
        assertTrue(r1.canEqual(r2));
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("9.00"))
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "not-an-approve-loan-request");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("45000.00"))
                .interestRate(new BigDecimal("10.00"))
                .build();

        String value = request.toString();

        assertNotNull(value);
        assertTrue(value.contains("ApproveLoanRequest"));
        // BigDecimal toString might be "45000.00" or "45000.0" or "45000"
        assertTrue(value.contains("45000"));
    }

    @Test
    void canEqual_shouldReturnFalseForDifferentClass() {
        ApproveLoanRequest request = ApproveLoanRequest.builder()
                .approvedAmount(new BigDecimal("15000.00"))
                .interestRate(new BigDecimal("8.75"))
                .build();

        assertFalse(request.canEqual("invalid-object"));
    }

}
