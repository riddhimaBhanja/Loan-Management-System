package com.loanmanagement.loanapp.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageConfigTest {

    @Test
    void defaultValues_shouldBeSetCorrectly() {
        FileStorageConfig config = new FileStorageConfig();

        assertEquals("./uploads/loan-documents", config.getUploadDir());
        assertEquals(10485760L, config.getMaxFileSize());
        assertArrayEquals(
                new String[]{"pdf", "jpg", "jpeg", "png", "doc", "docx"},
                config.getAllowedExtensions()
        );
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        FileStorageConfig config = new FileStorageConfig();

        config.setUploadDir("/tmp/uploads");
        config.setMaxFileSize(20971520L);
        config.setAllowedExtensions(new String[]{"txt"});

        assertEquals("/tmp/uploads", config.getUploadDir());
        assertEquals(20971520L, config.getMaxFileSize());
        assertArrayEquals(new String[]{"txt"}, config.getAllowedExtensions());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        FileStorageConfig c1 = new FileStorageConfig();
        c1.setUploadDir("/uploads");
        c1.setMaxFileSize(1000);
        c1.setAllowedExtensions(new String[]{"pdf"});

        FileStorageConfig c2 = new FileStorageConfig();
        c2.setUploadDir("/uploads");
        c2.setMaxFileSize(1000);
        c2.setAllowedExtensions(new String[]{"pdf"});

        FileStorageConfig c3 = new FileStorageConfig();
        c3.setUploadDir("/other");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }
    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        FileStorageConfig config = new FileStorageConfig();

        assertNotEquals(config, null);
        assertNotEquals(config, "not-a-config");
    }
    @Test
    void toString_shouldContainClassNameAndFields() {
        FileStorageConfig config = new FileStorageConfig();
        config.setUploadDir("/uploads");

        String value = config.toString();

        assertNotNull(value);
        assertTrue(value.contains("FileStorageConfig"));
        assertTrue(value.contains("/uploads"));
    }

}
