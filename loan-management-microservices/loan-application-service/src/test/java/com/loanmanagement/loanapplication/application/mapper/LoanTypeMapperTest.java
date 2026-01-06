package com.loanmanagement.loanapplication.application.mapper;

import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.application.mapper.LoanTypeMapper;
import com.loanmanagement.loanapp.domain.model.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoanTypeMapper Tests")
class LoanTypeMapperTest {

    private LoanTypeMapper loanTypeMapper;

    private LoanType loanType;
    private CreateLoanTypeRequest createRequest;
    private UpdateLoanTypeRequest updateRequest;

    @BeforeEach
    void setUp() {
        loanTypeMapper = Mappers.getMapper(LoanTypeMapper.class);

        loanType = new LoanType();
        loanType.setId(1L);
        loanType.setName("Home Loan");
        loanType.setDescription("Home loan description");
        loanType.setIsActive(true);
        loanType.setCreatedAt(LocalDateTime.now());
        loanType.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateLoanTypeRequest();
        createRequest.setName("Car Loan");
        createRequest.setDescription("Car loan description");
        createRequest.setIsActive(true);

        updateRequest = new UpdateLoanTypeRequest();
        updateRequest.setDescription("Updated description");
        updateRequest.setIsActive(false);
    }

    // ===================== TO RESPONSE =====================

    @Test
    @DisplayName("Should map LoanType to LoanTypeResponse")
    void toResponse_ShouldMapAllFields() {
        LoanTypeResponse response = loanTypeMapper.toResponse(loanType);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(loanType.getId());
        assertThat(response.getName()).isEqualTo(loanType.getName());
        assertThat(response.getDescription()).isEqualTo(loanType.getDescription());
        assertThat(response.getIsActive()).isEqualTo(loanType.getIsActive());
    }

    @Test
    @DisplayName("Should return null when LoanType is null")
    void toResponse_ShouldReturnNull_WhenLoanTypeIsNull() {
        LoanTypeResponse response = loanTypeMapper.toResponse(null);
        assertThat(response).isNull();
    }

    // ===================== CREATE REQUEST =====================

    @Test
    @DisplayName("Should map CreateLoanTypeRequest to LoanType entity")
    void toEntity_ShouldMapFields_FromCreateRequest() {
        LoanType entity = loanTypeMapper.toEntity(createRequest);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(createRequest.getName());
        assertThat(entity.getDescription()).isEqualTo(createRequest.getDescription());
        assertThat(entity.getIsActive()).isEqualTo(createRequest.getIsActive());
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should return null when CreateLoanTypeRequest is null")
    void toEntity_ShouldReturnNull_WhenCreateRequestIsNull() {
        LoanType entity = loanTypeMapper.toEntity(null);
        assertThat(entity).isNull();
    }

    // ===================== UPDATE REQUEST =====================

    @Test
    @DisplayName("Should update LoanType fields except ignored ones")
    void updateEntityFromRequest_ShouldUpdateAllowedFieldsOnly() {
        String originalName = loanType.getName();
        LocalDateTime originalCreatedAt = loanType.getCreatedAt();

        loanTypeMapper.updateEntityFromRequest(updateRequest, loanType);

        assertThat(loanType.getName()).isEqualTo(originalName); // ignored
        assertThat(loanType.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(loanType.getIsActive()).isEqualTo(updateRequest.getIsActive());
        assertThat(loanType.getCreatedAt()).isEqualTo(originalCreatedAt); // ignored
    }

    @Test
    @DisplayName("Should ignore null fields in UpdateLoanTypeRequest")
    void updateEntityFromRequest_ShouldIgnoreNullValues() {
        UpdateLoanTypeRequest emptyRequest = new UpdateLoanTypeRequest();

        loanTypeMapper.updateEntityFromRequest(emptyRequest, loanType);

        assertThat(loanType.getName()).isEqualTo("Home Loan");
        assertThat(loanType.getDescription()).isEqualTo("Home loan description");
        assertThat(loanType.getIsActive()).isTrue();
    }
}
