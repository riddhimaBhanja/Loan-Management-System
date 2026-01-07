package com.loanmanagement.loanapp.application.dto.request;

import com.loanmanagement.loanapp.application.dto.request.AssignOfficerRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssignOfficerRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenLoanOfficerIdIsPresent() {
        AssignOfficerRequest request = AssignOfficerRequest.builder()
                .loanOfficerId(1L)
                .remarks("Assigned")
                .build();

        Set<ConstraintViolation<AssignOfficerRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenLoanOfficerIdIsNull() {
        AssignOfficerRequest request = AssignOfficerRequest.builder()
                .remarks("Missing officer")
                .build();

        Set<ConstraintViolation<AssignOfficerRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        AssignOfficerRequest r1 = AssignOfficerRequest.builder()
                .loanOfficerId(5L)
                .remarks("Assigned")
                .build();

        AssignOfficerRequest r2 = AssignOfficerRequest.builder()
                .loanOfficerId(5L)
                .remarks("Assigned")
                .build();

        AssignOfficerRequest r3 = AssignOfficerRequest.builder()
                .loanOfficerId(10L)
                .remarks("Reassigned")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        AssignOfficerRequest request = AssignOfficerRequest.builder()
                .loanOfficerId(3L)
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "not-an-assign-officer-request");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        AssignOfficerRequest request = AssignOfficerRequest.builder()
                .loanOfficerId(9L)
                .remarks("Temporary assignment")
                .build();

        String value = request.toString();

        assertNotNull(value);
        assertTrue(value.contains("AssignOfficerRequest"));
        assertTrue(value.contains("9"));
    }
}
