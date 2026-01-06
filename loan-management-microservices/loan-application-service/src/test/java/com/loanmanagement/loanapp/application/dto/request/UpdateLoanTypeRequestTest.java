package com.loanmanagement.loanapp.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateLoanTypeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .description("Updated loan type description")
                .minAmount(BigDecimal.valueOf(50000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(6)
                .maxTenureMonths(120)
                .interestRate(BigDecimal.valueOf(9.5))
                .isActive(false)
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescriptionExceedsMaxLength() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .description("a".repeat(501))
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMinAmountIsZeroOrNegative() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .minAmount(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMaxAmountIsZeroOrNegative() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .maxAmount(BigDecimal.valueOf(-10))
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMinTenureIsInvalid() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .minTenureMonths(0)
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMaxTenureIsInvalid() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .maxTenureMonths(0)
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenInterestRateExceedsMax() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .interestRate(BigDecimal.valueOf(150))
                .build();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldAllowAllFieldsToBeNull() {
        UpdateLoanTypeRequest request = new UpdateLoanTypeRequest();

        Set<ConstraintViolation<UpdateLoanTypeRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
