package com.loanmanagement.loanapp.application.dto.request;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(250000))
                .tenureMonths(120)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(60000))
                .purpose("Home renovation")
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoanTypeIdIsNull() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .amount(BigDecimal.valueOf(100000))
                .tenureMonths(60)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(50000))
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.ZERO)
                .tenureMonths(60)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(50000))
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenTenureTooLowOrTooHigh() {
        LoanApplicationRequest lowTenure = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(100000))
                .tenureMonths(0)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(50000))
                .build();

        LoanApplicationRequest highTenure = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(100000))
                .tenureMonths(500)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(50000))
                .build();

        assertEquals(1, validator.validate(lowTenure).size());
        assertEquals(1, validator.validate(highTenure).size());
    }

    @Test
    void shouldFailWhenEmploymentStatusIsNull() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(150000))
                .tenureMonths(48)
                .monthlyIncome(BigDecimal.valueOf(45000))
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenMonthlyIncomeIsInvalid() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(150000))
                .tenureMonths(48)
                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                .monthlyIncome(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenPurposeExceedsMaxLength() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(200000))
                .tenureMonths(60)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(70000))
                .purpose("a".repeat(1001))
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldAllowPurposeToBeNull() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(200000))
                .tenureMonths(60)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(70000))
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
