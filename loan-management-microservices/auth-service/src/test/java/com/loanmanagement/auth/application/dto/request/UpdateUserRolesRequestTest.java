package com.loanmanagement.auth.application.dto.request;

import com.loanmanagement.auth.domain.model.RoleType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRolesRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenAtLeastOneRoleProvided() {
        UpdateUserRolesRequest request = UpdateUserRolesRequest.builder()
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<UpdateUserRolesRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenRolesIsEmpty() {
        UpdateUserRolesRequest request = UpdateUserRolesRequest.builder()
                .roles(Collections.emptySet())
                .build();

        Set<ConstraintViolation<UpdateUserRolesRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailValidationWhenRolesIsNull() {
        UpdateUserRolesRequest request = new UpdateUserRolesRequest();

        Set<ConstraintViolation<UpdateUserRolesRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }
}
