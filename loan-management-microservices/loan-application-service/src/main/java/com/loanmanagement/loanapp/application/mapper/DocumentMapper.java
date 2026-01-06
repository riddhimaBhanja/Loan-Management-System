package com.loanmanagement.loanapp.application.mapper;

import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.domain.model.Document;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Document entity
 */
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentResponse toResponse(Document document);
}
