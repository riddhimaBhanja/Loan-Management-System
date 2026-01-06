package com.loanmanagement.auth.application.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {

    @Test
    void shouldCreatePageResponseFromPage() {
        List<String> content = List.of("A", "B", "C");
        PageRequest pageable = PageRequest.of(0, 3);
        Page<String> page = new PageImpl<>(content, pageable, 3);

        PageResponse<String> response = PageResponse.from(page);

        assertEquals(content, response.getContent());
        assertEquals(3L, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getCurrentPage());
        assertEquals(3, response.getSize());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        assertFalse(response.getEmpty());
    }

    @Test
    void shouldCreatePageResponseWithMappedContent() {
        List<Integer> originalContent = List.of(1, 2, 3);
        List<String> mappedContent = List.of("One", "Two", "Three");

        PageRequest pageable = PageRequest.of(1, 3);
        Page<Integer> page = new PageImpl<>(originalContent, pageable, 10);

        PageResponse<String> response = PageResponse.from(page, mappedContent);

        assertEquals(mappedContent, response.getContent());
        assertEquals(10L, response.getTotalElements());
        assertEquals(4, response.getTotalPages());
        assertEquals(1, response.getCurrentPage());
        assertEquals(3, response.getSize());
        assertFalse(response.getFirst());
        assertFalse(response.getLast());
        assertFalse(response.getEmpty());
    }

    @Test
    void shouldHandleEmptyPage() {
        List<String> content = List.of();
        PageRequest pageable = PageRequest.of(0, 5);
        Page<String> page = new PageImpl<>(content, pageable, 0);

        PageResponse<String> response = PageResponse.from(page);

        assertTrue(response.getContent().isEmpty());
        assertEquals(0L, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getCurrentPage());
        assertEquals(5, response.getSize());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        assertTrue(response.getEmpty());
    }
}
