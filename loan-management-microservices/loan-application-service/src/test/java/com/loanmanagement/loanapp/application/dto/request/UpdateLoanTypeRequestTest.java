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
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        UpdateLoanTypeRequest r1 = UpdateLoanTypeRequest.builder()
                .description("Loan type")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(6)
                .maxTenureMonths(120)
                .interestRate(BigDecimal.valueOf(9.5))
                .lateFeePercentage(BigDecimal.valueOf(2.5))
                .gracePeriodDays(5)
                .isActive(true)
                .build();

        UpdateLoanTypeRequest r2 = UpdateLoanTypeRequest.builder()
                .description("Loan type")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(6)
                .maxTenureMonths(120)
                .interestRate(BigDecimal.valueOf(9.5))
                .lateFeePercentage(BigDecimal.valueOf(2.5))
                .gracePeriodDays(5)
                .isActive(true)
                .build();

        UpdateLoanTypeRequest r3 = UpdateLoanTypeRequest.builder()
                .description("Different")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .description("Loan type")
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "not-an-update-loan-type-request");
    }

    @Test
    void toString_shouldContainClassNameAndFields() {
        UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
                .description("Loan type")
                .minAmount(BigDecimal.valueOf(10000))
                .build();

        String value = request.toString();

        assertNotNull(value);
        assertTrue(value.contains("UpdateLoanTypeRequest"));
        assertTrue(value.contains("Loan type"));
    }

    @Test
    void constructors_shouldCreateObjectsSuccessfully() {
        UpdateLoanTypeRequest noArgs = new UpdateLoanTypeRequest();
        assertNotNull(noArgs);

        UpdateLoanTypeRequest allArgs = new UpdateLoanTypeRequest(
                "Loan type",
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500000),
                6,
                120,
                BigDecimal.valueOf(9.5),
                BigDecimal.valueOf(2.5),
                5,
                true
        );

        assertEquals("Loan type", allArgs.getDescription());
        assertEquals(BigDecimal.valueOf(10000), allArgs.getMinAmount());
        assertEquals(BigDecimal.valueOf(500000), allArgs.getMaxAmount());
        assertEquals(6, allArgs.getMinTenureMonths());
        assertEquals(120, allArgs.getMaxTenureMonths());
        assertEquals(BigDecimal.valueOf(9.5), allArgs.getInterestRate());
        assertEquals(BigDecimal.valueOf(2.5), allArgs.getLateFeePercentage());
        assertEquals(5, allArgs.getGracePeriodDays());
        assertTrue(allArgs.getIsActive());
    }

}
