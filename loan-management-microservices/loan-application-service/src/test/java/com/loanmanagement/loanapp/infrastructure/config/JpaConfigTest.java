package com.loanmanagement.loanapp.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

class JpaConfigTest {

    @Test
    void shouldBeConfigurationClass() {
        assertTrue(JpaConfig.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    void shouldEnableJpaAuditing() {
        assertTrue(JpaConfig.class.isAnnotationPresent(EnableJpaAuditing.class));
    }
}
