package com.loanmanagement.loanapp.application.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignOfficerRequestTest {

    @Test
    void noArgsConstructor_shouldCreateObject() {
        AssignOfficerRequest request = new AssignOfficerRequest();
        assertNotNull(request);
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        AssignOfficerRequest request =
                new AssignOfficerRequest(5L, "Assigned by admin");

        assertEquals(5L, request.getLoanOfficerId());
        assertEquals("Assigned by admin", request.getRemarks());
    }

    @Test
    void builder_shouldBuildObjectCorrectly() {
        AssignOfficerRequest request =
                AssignOfficerRequest.builder()
                        .loanOfficerId(10L)
                        .remarks("Reassigned")
                        .build();

        assertEquals(10L, request.getLoanOfficerId());
        assertEquals("Reassigned", request.getRemarks());
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setLoanOfficerId(7L);
        request.setRemarks("Test remarks");

        assertEquals(7L, request.getLoanOfficerId());
        assertEquals("Test remarks", request.getRemarks());
    }
}
