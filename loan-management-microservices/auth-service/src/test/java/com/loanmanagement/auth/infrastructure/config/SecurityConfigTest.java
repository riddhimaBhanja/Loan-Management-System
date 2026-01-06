package com.loanmanagement.auth.infrastructure.config;

import com.loanmanagement.auth.infrastructure.security.UserDetailsServiceImpl;
import com.loanmanagement.auth.infrastructure.security.jwt.JwtAuthenticationEntryPoint;
import com.loanmanagement.auth.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void passwordEncoderBean_shouldBeCreated() {
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        assertNotNull(passwordEncoder);
    }

    @Test
    void authenticationProviderBean_shouldBeCreated() {
        DaoAuthenticationProvider provider =
                applicationContext.getBean(DaoAuthenticationProvider.class);
        assertNotNull(provider);
    }

    @Test
    void authenticationManagerBean_shouldBeCreated() {
        AuthenticationManager authenticationManager =
                applicationContext.getBean(AuthenticationManager.class);
        assertNotNull(authenticationManager);
    }

    @Test
    void securityFilterChainBean_shouldBeCreated() {
        SecurityFilterChain filterChain =
                applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain);
    }
}
