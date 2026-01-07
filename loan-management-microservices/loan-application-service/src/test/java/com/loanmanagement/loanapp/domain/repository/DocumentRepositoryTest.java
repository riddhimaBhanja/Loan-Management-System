package com.loanmanagement.loanapp.domain.repository;

import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.model.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    private Document createDocument(Long loanId, String fileName) {
        return Document.builder()
                .loanId(loanId)
                .documentType(DocumentType.ID_PROOF)
                .fileName(fileName)
                .originalFileName(fileName)
                .filePath("/docs/" + fileName)
                .fileSize(1024L)
                .contentType("application/pdf")
                .uploadedBy(1L)
                .uploadedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByLoanId_shouldReturnDocuments() {
        documentRepository.save(createDocument(1L, "doc1.pdf"));
        documentRepository.save(createDocument(1L, "doc2.pdf"));

        List<Document> documents = documentRepository.findByLoanId(1L);

        assertEquals(2, documents.size());
    }

    @Test
    void findByLoanIdAndDocumentType_shouldReturnMatchingDocuments() {
        documentRepository.save(createDocument(2L, "id.pdf"));

        List<Document> documents =
                documentRepository.findByLoanIdAndDocumentType(2L, DocumentType.ID_PROOF);

        assertEquals(1, documents.size());
    }

    @Test
    void findByLoanIdAndDocumentTypeAndFileName_shouldReturnDocument() {
        documentRepository.save(createDocument(3L, "aadhaar.pdf"));

        Optional<Document> document =
                documentRepository.findByLoanIdAndDocumentTypeAndFileName(
                        3L, DocumentType.ID_PROOF, "aadhaar.pdf");

        assertTrue(document.isPresent());
        assertEquals("aadhaar.pdf", document.get().getFileName());
    }

    @Test
    void countByLoanId_shouldReturnCorrectCount() {
        documentRepository.save(createDocument(4L, "doc1.pdf"));
        documentRepository.save(createDocument(4L, "doc2.pdf"));
        documentRepository.save(createDocument(4L, "doc3.pdf"));

        long count = documentRepository.countByLoanId(4L);

        assertEquals(3, count);
    }
}
