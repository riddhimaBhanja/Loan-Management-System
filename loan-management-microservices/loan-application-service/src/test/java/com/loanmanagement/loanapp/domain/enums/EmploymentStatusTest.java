package com.loanmanagement.loanapp.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmploymentStatusTest {

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        EmploymentStatus status = EmploymentStatus.valueOf("SALARIED");
        assertEquals(EmploymentStatus.SALARIED, status);
    }

    @Test
    void getDisplayName_shouldReturnCorrectValue() {
        assertEquals("Salaried", EmploymentStatus.SALARIED.getDisplayName());
        assertEquals("Self Employed", EmploymentStatus.SELF_EMPLOYED.getDisplayName());
        assertEquals("Business Owner", EmploymentStatus.BUSINESS_OWNER.getDisplayName());
        assertEquals("Unemployed", EmploymentStatus.UNEMPLOYED.getDisplayName());
        assertEquals("Retired", EmploymentStatus.RETIRED.getDisplayName());
    }

    @Test
    void values_shouldContainAllStatuses() {
        EmploymentStatus[] values = EmploymentStatus.values();

        assertEquals(5, values.length);
        assertArrayEquals(
                new EmploymentStatus[]{
                        EmploymentStatus.SALARIED,
                        EmploymentStatus.SELF_EMPLOYED,
                        EmploymentStatus.BUSINESS_OWNER,
                        EmploymentStatus.UNEMPLOYED,
                        EmploymentStatus.RETIRED
                },
                values
        );
    }
}
