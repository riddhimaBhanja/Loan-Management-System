package com.loanmanagement.loanapp.application.dto.response;

import com.loanmanagement.loanapp.domain.enums.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentResponseTest {

    @Test
    void shouldCreateDocumentResponseWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        DocumentResponse response = DocumentResponse.builder()
                .id(1L)
                .loanId(100L)
                .documentType(DocumentType.ID_PROOF)
                .fileName("aadhaar_100.pdf")
                .originalFileName("aadhaar.pdf")
                .filePath("/uploads/docs/aadhaar_100.pdf")
                .fileSize(2048L)
                .contentType("application/pdf")
                .uploadedAt(now)
                .uploadedBy(10L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(100L, response.getLoanId());
        assertEquals(DocumentType.ID_PROOF, response.getDocumentType());
        assertEquals("aadhaar_100.pdf", response.getFileName());
        assertEquals("aadhaar.pdf", response.getOriginalFileName());
        assertEquals("/uploads/docs/aadhaar_100.pdf", response.getFilePath());
        assertEquals(2048L, response.getFileSize());
        assertEquals("application/pdf", response.getContentType());
        assertEquals(now, response.getUploadedAt());
        assertEquals(10L, response.getUploadedBy());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        DocumentResponse response = new DocumentResponse();

        assertNull(response.getId());
        assertNull(response.getLoanId());
        assertNull(response.getDocumentType());
        assertNull(response.getFileName());
        assertNull(response.getOriginalFileName());
        assertNull(response.getFilePath());
        assertNull(response.getFileSize());
        assertNull(response.getContentType());
        assertNull(response.getUploadedAt());
        assertNull(response.getUploadedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        DocumentResponse response = new DocumentResponse(
                2L,
                200L,
                DocumentType.ID_PROOF,
                "pan_200.pdf",
                "pan.pdf",
                "/uploads/docs/pan_200.pdf",
                4096L,
                "application/pdf",
                now,
                20L,
                now,
                now
        );

        assertEquals(2L, response.getId());
        assertEquals(200L, response.getLoanId());
        assertEquals(DocumentType.ID_PROOF, response.getDocumentType());
        assertEquals("pan_200.pdf", response.getFileName());
        assertEquals("pan.pdf", response.getOriginalFileName());
        assertEquals("/uploads/docs/pan_200.pdf", response.getFilePath());
        assertEquals(4096L, response.getFileSize());
        assertEquals("application/pdf", response.getContentType());
        assertEquals(now, response.getUploadedAt());
        assertEquals(20L, response.getUploadedBy());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        DocumentResponse r1 = DocumentResponse.builder()
                .id(1L)
                .loanId(100L)
                .documentType(DocumentType.ID_PROOF)
                .fileName("doc.pdf")
                .fileSize(2048L)
                .uploadedAt(now)
                .build();

        DocumentResponse r2 = DocumentResponse.builder()
                .id(1L)
                .loanId(100L)
                .documentType(DocumentType.ID_PROOF)
                .fileName("doc.pdf")
                .fileSize(2048L)
                .uploadedAt(now)
                .build();

        DocumentResponse r3 = DocumentResponse.builder()
                .id(2L)
                .loanId(200L)
                .documentType(DocumentType.INCOME_PROOF)
                .fileName("other.pdf")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        DocumentResponse response = DocumentResponse.builder()
                .id(1L)
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "not-a-document-response");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        DocumentResponse response = DocumentResponse.builder()
                .id(1L)
                .fileName("doc.pdf")
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("DocumentResponse"));
        assertTrue(value.contains("doc.pdf"));
    }
    @Test
    void settersAndCanEqual_shouldWorkCorrectly() {
        DocumentResponse response = new DocumentResponse();

        response.setId(1L);
        response.setLoanId(100L);
        response.setDocumentType(DocumentType.ID_PROOF);
        response.setFileName("file.pdf");
        response.setOriginalFileName("original.pdf");
        response.setFilePath("/path/file.pdf");
        response.setFileSize(1024L);
        response.setContentType("application/pdf");
        response.setUploadedAt(LocalDateTime.now());
        response.setUploadedBy(10L);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        // getters invoked implicitly via assertions
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getLoanId());
        assertEquals(DocumentType.ID_PROOF, response.getDocumentType());
        assertEquals("file.pdf", response.getFileName());
        assertEquals("original.pdf", response.getOriginalFileName());
        assertEquals("/path/file.pdf", response.getFilePath());
        assertEquals(1024L, response.getFileSize());
        assertEquals("application/pdf", response.getContentType());
        assertEquals(10L, response.getUploadedBy());

        // canEqual path
        assertTrue(response.canEqual(new DocumentResponse()));
    }


}
