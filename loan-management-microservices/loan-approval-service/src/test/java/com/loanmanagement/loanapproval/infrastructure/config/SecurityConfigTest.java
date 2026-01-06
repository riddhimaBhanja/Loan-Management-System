package com.loanmanagement.loanapproval.infrastructure.config;

import com.loanmanagement.loanapproval.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void securityFilterChain_beanCreatedSuccessfully() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();

        context.registerBean(JwtAuthenticationFilter.class, () -> jwtAuthenticationFilter);
        context.register(SecurityConfig.class);
        context.refresh();

        SecurityFilterChain securityFilterChain =
                context.getBean(SecurityFilterChain.class);

        assertNotNull(securityFilterChain);

        context.close();
    }
}
