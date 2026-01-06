package com.loanmanagement.loanapplication.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.loanapp.application.controller.LoanTypeController;
import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.domain.service.LoanTypeService;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanTypeService loanTypeService;

    // ================= CREATE =================

    @Test
    @DisplayName("Should create loan type")
    void shouldCreateLoanType() throws Exception {
        CreateLoanTypeRequest request = new CreateLoanTypeRequest();
        request.setName("Home Loan");
        request.setDescription("Housing loan");
        request.setIsActive(true);

        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .description("Housing loan")
                .isActive(true)
                .build();

        when(loanTypeService.createLoanType(any())).thenReturn(response);

        mockMvc.perform(post("/api/loan-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_TYPE_CREATED));
    }

    // ================= GET BY ID =================

    @Test
    void shouldGetLoanTypeById() throws Exception {
        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(1L)
                .name("Car Loan")
                .isActive(true)
                .build();

        when(loanTypeService.getLoanTypeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/loan-types/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_TYPE_FETCHED));
    }

    // ================= GET ALL =================

    @Test
    void shouldGetAllLoanTypes() throws Exception {
        when(loanTypeService.getAllLoanTypes())
                .thenReturn(List.of(new LoanTypeResponse()));

        mockMvc.perform(get("/api/loan-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_TYPES_FETCHED));
    }

    // ================= GET ACTIVE =================

    @Test
    void shouldGetActiveLoanTypes() throws Exception {
        when(loanTypeService.getActiveLoanTypes())
                .thenReturn(List.of(new LoanTypeResponse()));

        mockMvc.perform(get("/api/loan-types/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_TYPES_FETCHED));
    }

    // ================= UPDATE =================

    @Test
    void shouldUpdateLoanType() throws Exception {
    	UpdateLoanTypeRequest request = UpdateLoanTypeRequest.builder()
    	        .description("Updated Description")
    	        .isActive(true)
    	        .build();


        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(1L)
                .name("Updated Loan")
                .isActive(true)
                .build();

        when(loanTypeService.updateLoanType(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/loan-types/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_TYPE_UPDATED));
    }

    // ================= DELETE =================

    @Test
    void shouldDeleteLoanType() throws Exception {
        doNothing().when(loanTypeService).deleteLoanType(1L);

        mockMvc.perform(delete("/api/loan-types/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(MessageConstants.LOAN_TYPE_DELETED));
    }
}
