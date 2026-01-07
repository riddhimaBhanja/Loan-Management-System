package com.loanmanagement.loanapp.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(OpenApiConfig.class);

    @Test
    void openApiBean_shouldLoadSuccessfully() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("loanApplicationServiceOpenAPI"));
        });
    }

    @Test
    void openApiBean_shouldHaveExpectedConfiguration() {
        OpenApiConfig config = new OpenApiConfig();
        ReflectionTestUtils.setField(config, "serverPort", "8082");

        OpenAPI openAPI = config.loanApplicationServiceOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Loan Application Service API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());

        assertNotNull(openAPI.getServers());
        assertEquals(2, openAPI.getServers().size());

        assertNotNull(openAPI.getComponents());
        SecurityScheme scheme =
                openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");

        assertNotNull(scheme);
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
    }

    @Test
    void securityRequirement_shouldBePresent() {
        OpenApiConfig config = new OpenApiConfig();
        ReflectionTestUtils.setField(config, "serverPort", "8082");

        OpenAPI openAPI = config.loanApplicationServiceOpenAPI();

        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        assertTrue(openAPI.getSecurity().get(0).containsKey("Bearer Authentication"));
    }
}
