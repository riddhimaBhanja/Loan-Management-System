package com.loanmanagement.loanapp.infrastructure.config;

import com.loanmanagement.loanapp.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void securityFilterChainBean_shouldBeCreated() {
        SecurityFilterChain filterChain =
                applicationContext.getBean(SecurityFilterChain.class);

        assertNotNull(filterChain);
    }
}
