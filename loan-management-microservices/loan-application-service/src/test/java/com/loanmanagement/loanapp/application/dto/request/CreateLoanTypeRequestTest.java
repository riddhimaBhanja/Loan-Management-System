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

class CreateLoanTypeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Home Loan")
                .description("Long term home loan")
                .minAmount(BigDecimal.valueOf(100000))
                .maxAmount(BigDecimal.valueOf(5000000))
                .minTenureMonths(12)
                .maxTenureMonths(360)
                .interestRate(BigDecimal.valueOf(8.5))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertTrue(request.getIsActive());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name(" ")
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(2000))
                .minTenureMonths(1)
                .maxTenureMonths(12)
                .interestRate(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMinAmountIsNull() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Car Loan")
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .interestRate(BigDecimal.valueOf(9))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMaxAmountIsZero() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Car Loan")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.ZERO)
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .interestRate(BigDecimal.valueOf(9))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenTenureIsInvalid() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Education Loan")
                .minAmount(BigDecimal.valueOf(50000))
                .maxAmount(BigDecimal.valueOf(2000000))
                .minTenureMonths(0)
                .maxTenureMonths(0)
                .interestRate(BigDecimal.valueOf(7))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void shouldFailWhenInterestRateExceedsMax() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Personal Loan")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(300000))
                .minTenureMonths(6)
                .maxTenureMonths(48)
                .interestRate(BigDecimal.valueOf(150))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldAllowDescriptionToBeNull() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Gold Loan")
                .minAmount(BigDecimal.valueOf(5000))
                .maxAmount(BigDecimal.valueOf(200000))
                .minTenureMonths(3)
                .maxTenureMonths(36)
                .interestRate(BigDecimal.valueOf(10))
                .build();

        Set<ConstraintViolation<CreateLoanTypeRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
