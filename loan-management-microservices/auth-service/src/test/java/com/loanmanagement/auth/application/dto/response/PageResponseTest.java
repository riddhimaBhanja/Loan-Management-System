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
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        PageResponse<String> r1 = PageResponse.<String>builder()
                .content(List.of("A", "B"))
                .totalElements(2L)
                .totalPages(1)
                .currentPage(0)
                .size(2)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        PageResponse<String> r2 = PageResponse.<String>builder()
                .content(List.of("A", "B"))
                .totalElements(2L)
                .totalPages(1)
                .currentPage(0)
                .size(2)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        PageResponse<String> r3 = PageResponse.<String>builder()
                .content(List.of("X"))
                .totalElements(1L)
                .totalPages(1)
                .currentPage(0)
                .size(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        PageResponse<String> response = PageResponse.<String>builder()
                .content(List.of("A"))
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "not-a-page-response");
    }

    @Test
    void toString_shouldContainClassNameAndFields() {
        PageResponse<String> response = PageResponse.<String>builder()
                .content(List.of("A", "B"))
                .totalElements(2L)
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("PageResponse"));
        assertTrue(value.contains("content"));
        assertTrue(value.contains("totalElements"));
    }

    @Test
    void setters_shouldUpdateBooleanFlags() {
        PageResponse<String> response = new PageResponse<>();

        response.setFirst(true);
        response.setLast(false);
        response.setEmpty(true);

        assertTrue(response.getFirst());
        assertFalse(response.getLast());
        assertTrue(response.getEmpty());
    }

    @Test
    void constructors_shouldCreateObjectsSuccessfully() {
        PageResponse<String> noArgs = new PageResponse<>();
        assertNotNull(noArgs);

        PageResponse<String> allArgs = new PageResponse<>(
                List.of("A"),
                1L,
                1,
                0,
                1,
                true,
                true,
                false
        );

        assertEquals(List.of("A"), allArgs.getContent());
        assertEquals(1L, allArgs.getTotalElements());
        assertEquals(1, allArgs.getTotalPages());
        assertEquals(0, allArgs.getCurrentPage());
        assertEquals(1, allArgs.getSize());
        assertTrue(allArgs.getFirst());
        assertTrue(allArgs.getLast());
        assertFalse(allArgs.getEmpty());
    }

}
