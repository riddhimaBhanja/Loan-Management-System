package com.loanmanagement.loanapp.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTypeTest {

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        DocumentType type = DocumentType.valueOf("ID_PROOF");
        assertEquals(DocumentType.ID_PROOF, type);
    }

    @Test
    void values_shouldContainAllDocumentTypes() {
        DocumentType[] values = DocumentType.values();

        assertEquals(7, values.length);
        assertArrayEquals(
                new DocumentType[]{
                        DocumentType.ID_PROOF,
                        DocumentType.INCOME_PROOF,
                        DocumentType.ADDRESS_PROOF,
                        DocumentType.BANK_STATEMENT,
                        DocumentType.EMPLOYMENT_PROOF,
                        DocumentType.BUSINESS_PROOF,
                        DocumentType.OTHER
                },
                values
        );
    }

    @Test
    void name_shouldMatchEnumConstant() {
        assertEquals("BANK_STATEMENT", DocumentType.BANK_STATEMENT.name());
    }
}
