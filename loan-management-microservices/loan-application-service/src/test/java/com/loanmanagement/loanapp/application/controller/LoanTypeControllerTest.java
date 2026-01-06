package com.loanmanagement.loanapp.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.domain.service.LoanTypeService;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanTypeController.class)
class LoanTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanTypeService loanTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createLoanType_success() throws Exception {
        CreateLoanTypeRequest request = new CreateLoanTypeRequest();

        Mockito.when(loanTypeService.createLoanType(Mockito.any()))
                .thenReturn(new LoanTypeResponse());

        Authentication authentication =
                new TestingAuthenticationToken("admin", null, "ROLE_ADMIN");

        mockMvc.perform(post("/api/loan-types")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_TYPE_CREATED))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void getLoanTypeById_success() throws Exception {
        Mockito.when(loanTypeService.getLoanTypeById(1L))
                .thenReturn(new LoanTypeResponse());

        mockMvc.perform(get("/api/loan-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_TYPE_FETCHED));
    }

    @Test
    void getAllLoanTypes_success() throws Exception {
        Mockito.when(loanTypeService.getAllLoanTypes())
                .thenReturn(List.of(new LoanTypeResponse()));

        mockMvc.perform(get("/api/loan-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_TYPES_FETCHED))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getActiveLoanTypes_success() throws Exception {
        Mockito.when(loanTypeService.getActiveLoanTypes())
                .thenReturn(List.of(new LoanTypeResponse()));

        mockMvc.perform(get("/api/loan-types/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_TYPES_FETCHED))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void updateLoanType_success() throws Exception {
        UpdateLoanTypeRequest request = new UpdateLoanTypeRequest();

        Mockito.when(loanTypeService.updateLoanType(Mockito.eq(1L), Mockito.any()))
                .thenReturn(new LoanTypeResponse());

        Authentication authentication =
                new TestingAuthenticationToken("admin", null, "ROLE_ADMIN");

        mockMvc.perform(put("/api/loan-types/1")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_TYPE_UPDATED))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void deleteLoanType_success() throws Exception {
        Authentication authentication =
                new TestingAuthenticationToken("admin", null, "ROLE_ADMIN");

        mockMvc.perform(delete("/api/loan-types/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.LOAN_TYPE_DELETED));
    }
}
