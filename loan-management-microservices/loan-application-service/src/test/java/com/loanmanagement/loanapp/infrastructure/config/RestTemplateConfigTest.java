package com.loanmanagement.loanapp.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestTemplateConfigTest {

    @Test
    void restTemplateBean_shouldBeCreated() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(RestTemplateConfig.class);

        RestTemplate restTemplate = context.getBean(RestTemplate.class);

        assertNotNull(restTemplate);

        context.close();
    }
}
