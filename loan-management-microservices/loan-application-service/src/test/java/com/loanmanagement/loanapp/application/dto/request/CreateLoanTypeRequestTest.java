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
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        CreateLoanTypeRequest r1 = CreateLoanTypeRequest.builder()
                .name("Home Loan")
                .minAmount(BigDecimal.valueOf(100000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(12)
                .maxTenureMonths(240)
                .interestRate(BigDecimal.valueOf(8.5))
                .build();

        CreateLoanTypeRequest r2 = CreateLoanTypeRequest.builder()
                .name("Home Loan")
                .minAmount(BigDecimal.valueOf(100000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(12)
                .maxTenureMonths(240)
                .interestRate(BigDecimal.valueOf(8.5))
                .build();

        CreateLoanTypeRequest r3 = CreateLoanTypeRequest.builder()
                .name("Car Loan")
                .minAmount(BigDecimal.valueOf(50000))
                .maxAmount(BigDecimal.valueOf(300000))
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .interestRate(BigDecimal.valueOf(9))
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertTrue(r1.canEqual(r2));
    }
    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Test Loan")
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(5000))
                .minTenureMonths(6)
                .maxTenureMonths(12)
                .interestRate(BigDecimal.valueOf(5))
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "not-a-loan-type-request");
    }
    @Test
    void toString_shouldContainClassNameAndFields() {
        CreateLoanTypeRequest request = CreateLoanTypeRequest.builder()
                .name("Home Loan")
                .interestRate(BigDecimal.valueOf(8.5))
                .build();

        String value = request.toString();

        assertNotNull(value);
        assertTrue(value.contains("CreateLoanTypeRequest"));
        assertTrue(value.contains("Home Loan"));
    }
    @Test
    void settersAndDefaults_shouldWorkCorrectly() {
        CreateLoanTypeRequest request = new CreateLoanTypeRequest();

        request.setName("Education Loan");
        request.setDescription("Student loan");
        request.setMinAmount(BigDecimal.valueOf(50000));
        request.setMaxAmount(BigDecimal.valueOf(2000000));
        request.setMinTenureMonths(12);
        request.setMaxTenureMonths(120);
        request.setInterestRate(BigDecimal.valueOf(7.5));
        request.setLateFeePercentage(BigDecimal.valueOf(3));
        request.setGracePeriodDays(5);
        request.setIsActive(false);

        assertEquals("Education Loan", request.getName());
        assertEquals(BigDecimal.valueOf(3), request.getLateFeePercentage());
        assertEquals(5, request.getGracePeriodDays());
        assertFalse(request.getIsActive());
    }


}
