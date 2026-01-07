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
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        LoanApplicationRequest request = new LoanApplicationRequest();

        request.setLoanTypeId(5L);
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTenureMonths(180);
        request.setEmploymentStatus(EmploymentStatus.SALARIED);
        request.setMonthlyIncome(BigDecimal.valueOf(80000));
        request.setPurpose("House construction");

        assertEquals(5L, request.getLoanTypeId());
        assertEquals(BigDecimal.valueOf(300000), request.getAmount());
        assertEquals(180, request.getTenureMonths());
        assertEquals(EmploymentStatus.SALARIED, request.getEmploymentStatus());
        assertEquals(BigDecimal.valueOf(80000), request.getMonthlyIncome());
        assertEquals("House construction", request.getPurpose());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LoanApplicationRequest r1 = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(200000))
                .tenureMonths(60)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(50000))
                .purpose("Home loan")
                .build();

        LoanApplicationRequest r2 = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(200000))
                .tenureMonths(60)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(50000))
                .purpose("Home loan")
                .build();

        LoanApplicationRequest r3 = LoanApplicationRequest.builder()
                .loanTypeId(2L)
                .amount(BigDecimal.valueOf(100000))
                .tenureMonths(24)
                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                .monthlyIncome(BigDecimal.valueOf(40000))
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(100000))
                .tenureMonths(12)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(30000))
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "not-a-loan-application-request");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(9L)
                .amount(BigDecimal.valueOf(450000))
                .build();

        String value = request.toString();

        assertNotNull(value);
        assertTrue(value.contains("LoanApplicationRequest"));
        assertTrue(value.contains("450000"));
    }

    @Test
    void allArgsConstructor_shouldCreateObjectSuccessfully() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                3L,
                BigDecimal.valueOf(150000),
                36,
                EmploymentStatus.SELF_EMPLOYED,
                BigDecimal.valueOf(55000),
                "Business expansion"
        );

        assertEquals(3L, request.getLoanTypeId());
        assertEquals(BigDecimal.valueOf(150000), request.getAmount());
        assertEquals(36, request.getTenureMonths());
        assertEquals(EmploymentStatus.SELF_EMPLOYED, request.getEmploymentStatus());
        assertEquals(BigDecimal.valueOf(55000), request.getMonthlyIncome());
        assertEquals("Business expansion", request.getPurpose());
    }

    @Test
    void hashCode_shouldBeStableAcrossCalls() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(7L)
                .amount(BigDecimal.valueOf(99999))
                .tenureMonths(48)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(60000))
                .build();

        int hash1 = request.hashCode();
        int hash2 = request.hashCode();

        assertEquals(hash1, hash2);
    }

}
