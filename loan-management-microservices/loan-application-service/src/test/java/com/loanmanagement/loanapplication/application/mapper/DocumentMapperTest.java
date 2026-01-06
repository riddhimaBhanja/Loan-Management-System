package com.loanmanagement.loanapplication.application.mapper;

import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.application.mapper.DocumentMapper;
import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.model.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DocumentMapper Tests")
class DocumentMapperTest {

    private final DocumentMapper documentMapper =
            Mappers.getMapper(DocumentMapper.class);

    @Test
    @DisplayName("Should map Document entity to DocumentResponse")
    void toResponse_ShouldMapAllFields_WhenDocumentProvided() {
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setLoanId(100L);
        document.setDocumentType(DocumentType.ID_PROOF);
        document.setOriginalFileName("aadhar.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(2048L);
        document.setUploadedBy(10L);
        document.setUploadedAt(LocalDateTime.now());

        // When
        DocumentResponse response = documentMapper.toResponse(document);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(document.getId());
        assertThat(response.getLoanId()).isEqualTo(document.getLoanId());
        assertThat(response.getDocumentType()).isEqualTo(document.getDocumentType());
        assertThat(response.getOriginalFileName()).isEqualTo(document.getOriginalFileName());
        assertThat(response.getContentType()).isEqualTo(document.getContentType());
        assertThat(response.getFileSize()).isEqualTo(document.getFileSize());
        assertThat(response.getUploadedBy()).isEqualTo(document.getUploadedBy());
        assertThat(response.getUploadedAt()).isEqualTo(document.getUploadedAt());
    }

    @Test
    @DisplayName("Should return null when Document is null")
    void toResponse_ShouldReturnNull_WhenDocumentIsNull() {
        // When
        DocumentResponse response = documentMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }
}
