package com.loanmanagement.loanapp.application.mapper;

import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.domain.model.Document;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentMapperTest {

    private final DocumentMapper documentMapper =
            Mappers.getMapper(DocumentMapper.class);

    @Test
    void toResponse_shouldMapEntityToResponse() {
        Document document = new Document();

        DocumentResponse response = documentMapper.toResponse(document);

        assertNotNull(response);
    }
}
