package com.loanmanagement.loanapp.domain.model;

import com.loanmanagement.loanapp.domain.enums.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void noArgsConstructor_shouldCreateObject() {
        Document document = new Document();
        assertNotNull(document);
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();

        Document document = new Document(
                1L,
                10L,
                DocumentType.ID_PROOF,
                "file.pdf",
                "original.pdf",
                "/files/file.pdf",
                1024L,
                "application/pdf",
                now,
                5L,
                now,
                now
        );

        assertEquals(1L, document.getId());
        assertEquals(10L, document.getLoanId());
        assertEquals(DocumentType.ID_PROOF, document.getDocumentType());
        assertEquals("file.pdf", document.getFileName());
        assertEquals("original.pdf", document.getOriginalFileName());
        assertEquals("/files/file.pdf", document.getFilePath());
        assertEquals(1024L, document.getFileSize());
        assertEquals("application/pdf", document.getContentType());
        assertEquals(now, document.getUploadedAt());
        assertEquals(5L, document.getUploadedBy());
        assertEquals(now, document.getCreatedAt());
        assertEquals(now, document.getUpdatedAt());
    }

    @Test
    void builder_shouldBuildObjectCorrectly() {
        Document document = Document.builder()
                .loanId(20L)
                .documentType(DocumentType.BANK_STATEMENT)
                .fileName("statement.pdf")
                .originalFileName("bank_statement.pdf")
                .filePath("/docs/statement.pdf")
                .fileSize(2048L)
                .contentType("application/pdf")
                .uploadedBy(8L)
                .build();

        assertEquals(20L, document.getLoanId());
        assertEquals(DocumentType.BANK_STATEMENT, document.getDocumentType());
        assertEquals("statement.pdf", document.getFileName());
        assertEquals("bank_statement.pdf", document.getOriginalFileName());
        assertEquals("/docs/statement.pdf", document.getFilePath());
        assertEquals(2048L, document.getFileSize());
        assertEquals("application/pdf", document.getContentType());
        assertEquals(8L, document.getUploadedBy());
    }

    @Test
    void prePersist_shouldInitializeTimestamps() {
        Document document = new Document();
        document.setLoanId(1L);
        document.setDocumentType(DocumentType.OTHER);
        document.setFileName("doc.txt");
        document.setOriginalFileName("doc.txt");
        document.setFilePath("/docs/doc.txt");
        document.setFileSize(100L);
        document.setContentType("text/plain");
        document.setUploadedBy(2L);

        document.onCreate();

        assertNotNull(document.getCreatedAt());
        assertNotNull(document.getUpdatedAt());
        assertNotNull(document.getUploadedAt());
    }

    @Test
    void preUpdate_shouldUpdateUpdatedAt() {
        Document document = new Document();
        LocalDateTime oldTime = LocalDateTime.now().minusDays(1);
        document.setUpdatedAt(oldTime);

        document.onUpdate();

        assertNotNull(document.getUpdatedAt());
        assertTrue(document.getUpdatedAt().isAfter(oldTime));
    }
}
