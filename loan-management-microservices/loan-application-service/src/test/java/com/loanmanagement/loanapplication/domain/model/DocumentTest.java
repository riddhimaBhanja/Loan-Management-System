package com.loanmanagement.loanapplication.domain.model;

import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.model.Document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Document Entity Tests")
class DocumentTest {

    @Test
    @DisplayName("Should create Document using builder")
    void shouldCreateDocumentUsingBuilder() {
        LocalDateTime now = LocalDateTime.now();

        Document document = Document.builder()
                .id(1L)
                .loanId(100L)
                .documentType(DocumentType.ID_PROOF)
                .fileName("id_123.pdf")
                .originalFileName("aadhaar.pdf")
                .filePath("/uploads/loan/100/id_123.pdf")
                .fileSize(2048L)
                .contentType("application/pdf")
                .uploadedAt(now)
                .uploadedBy(10L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(document).isNotNull();
        assertThat(document.getLoanId()).isEqualTo(100L);
        assertThat(document.getDocumentType()).isEqualTo(DocumentType.ID_PROOF);
        assertThat(document.getFileName()).isEqualTo("id_123.pdf");
        assertThat(document.getOriginalFileName()).isEqualTo("aadhaar.pdf");
        assertThat(document.getFilePath()).isEqualTo("/uploads/loan/100/id_123.pdf");
        assertThat(document.getFileSize()).isEqualTo(2048L);
        assertThat(document.getContentType()).isEqualTo("application/pdf");
        assertThat(document.getUploadedBy()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should create Document with timestamps")
    void shouldCreateDocumentWithTimestamps() {
        LocalDateTime now = LocalDateTime.now();

        Document document = Document.builder()
                .loanId(200L)
                .documentType(DocumentType.ADDRESS_PROOF)
                .fileName("address.pdf")
                .originalFileName("address_original.pdf")
                .filePath("/uploads/loan/200/address.pdf")
                .fileSize(4096L)
                .contentType("application/pdf")
                .uploadedBy(20L)
                .createdAt(now)
                .updatedAt(now)
                .uploadedAt(now)
                .build();

        assertThat(document.getCreatedAt()).isNotNull();
        assertThat(document.getUpdatedAt()).isNotNull();
        assertThat(document.getUploadedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should preserve existing timestamps")
    void shouldPreserveExistingTimestamps() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);

        Document document = Document.builder()
                .loanId(300L)
                .documentType(DocumentType.INCOME_PROOF)
                .fileName("income.pdf")
                .originalFileName("income_original.pdf")
                .filePath("/uploads/loan/300/income.pdf")
                .fileSize(1024L)
                .contentType("application/pdf")
                .uploadedBy(30L)
                .createdAt(past)
                .updatedAt(past)
                .uploadedAt(past)
                .build();

        assertThat(document.getCreatedAt()).isEqualTo(past);
        assertThat(document.getUpdatedAt()).isEqualTo(past);
        assertThat(document.getUploadedAt()).isEqualTo(past);
    }

    @Test
    @DisplayName("Should create Document with OTHER type")
    void shouldCreateDocumentWithOtherType() {
        Document document = Document.builder()
                .loanId(400L)
                .documentType(DocumentType.OTHER)
                .fileName("other.pdf")
                .originalFileName("other_original.pdf")
                .filePath("/uploads/loan/400/other.pdf")
                .fileSize(512L)
                .contentType("application/pdf")
                .uploadedBy(40L)
                .build();

        assertThat(document.getDocumentType()).isEqualTo(DocumentType.OTHER);
        assertThat(document.getLoanId()).isEqualTo(400L);
    }
}
