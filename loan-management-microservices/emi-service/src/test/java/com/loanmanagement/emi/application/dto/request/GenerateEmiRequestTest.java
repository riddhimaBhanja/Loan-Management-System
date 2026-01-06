package com.loanmanagement.emi.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GenerateEmiRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(9.5))
                .tenureMonths(120)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoanIdIsNull() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(12)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenCustomerIdIsNull() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(12)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenPrincipalIsZeroOrNegative() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.ZERO)
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(12)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenInterestRateIsZero() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.ZERO)
                .tenureMonths(12)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenInterestRateExceedsMax() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(150))
                .tenureMonths(12)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenTenureTooLow() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(0)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenTenureTooHigh() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(700)
                .startDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenStartDateIsNull() {
        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(1L)
                .customerId(10L)
                .principal(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(10))
                .tenureMonths(12)
                .build();

        Set<ConstraintViolation<GenerateEmiRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }
}
