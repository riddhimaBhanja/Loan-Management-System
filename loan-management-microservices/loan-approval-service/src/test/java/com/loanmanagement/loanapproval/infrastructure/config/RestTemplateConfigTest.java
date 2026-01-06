package com.loanmanagement.loanapproval.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    void restTemplateBean_createdSuccessfully() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(RestTemplateConfig.class);

        RestTemplate restTemplate = context.getBean(RestTemplate.class);

        assertNotNull(restTemplate);

        boolean hasLoadBalanced =
                restTemplate.getClass()
                        .getAnnotationsByType(LoadBalanced.class)
                        .length > 0
                        || context.findAnnotationOnBean(
                        context.getBeanNamesForType(RestTemplate.class)[0],
                        LoadBalanced.class
                ) != null;

        assertTrue(hasLoadBalanced);

        context.close();
    }
}
