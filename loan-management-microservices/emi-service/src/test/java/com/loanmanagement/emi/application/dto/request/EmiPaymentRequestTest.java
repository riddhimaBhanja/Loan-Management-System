package com.loanmanagement.emi.application.dto.request;

import com.loanmanagement.emi.domain.model.PaymentMethod;
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

class EmiPaymentRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.valueOf(5000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference("TXN123")
                .remarks("Monthly EMI payment")
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmiScheduleIdIsNull() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .amount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenAmountIsNull() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.ZERO)
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenPaymentDateIsNull() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.valueOf(1000))
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenPaymentMethodIsNull() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenTransactionReferenceTooLong() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference("a".repeat(101))
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenRemarksTooLong() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .remarks("a".repeat(501))
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {
        EmiPaymentRequest request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Set<ConstraintViolation<EmiPaymentRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
