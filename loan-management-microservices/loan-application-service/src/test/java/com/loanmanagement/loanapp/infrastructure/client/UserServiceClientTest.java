package com.loanmanagement.loanapp.infrastructure.client;

import com.loanmanagement.common.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceClient userServiceClient;

    private static final String AUTH_SERVICE_URL = "http://auth-service";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                userServiceClient,
                "authServiceUrl",
                AUTH_SERVICE_URL
        );
    }

    @Test
    void getUserById_success() {
        UserResponse userResponse = new UserResponse();
        Long userId = 1L;

        when(restTemplate.getForObject(
                AUTH_SERVICE_URL + "/api/internal/users/" + userId,
                UserResponse.class
        )).thenReturn(userResponse);

        UserResponse response = userServiceClient.getUserById(userId);

        assertNotNull(response);
    }

    @Test
    void getUserById_shouldReturnNull_whenExceptionOccurs() {
        Long userId = 2L;

        when(restTemplate.getForObject(
                eq(AUTH_SERVICE_URL + "/api/internal/users/" + userId),
                eq(UserResponse.class)
        )).thenThrow(new RuntimeException("Service unavailable"));

        UserResponse response = userServiceClient.getUserById(userId);

        assertNull(response);
    }

    @Test
    void userExists_shouldReturnTrue_whenUserExists() {
        UserResponse userResponse = new UserResponse();
        Long userId = 3L;

        when(restTemplate.getForObject(
                AUTH_SERVICE_URL + "/api/internal/users/" + userId,
                UserResponse.class
        )).thenReturn(userResponse);

        boolean exists = userServiceClient.userExists(userId);

        assertTrue(exists);
    }

    @Test
    void userExists_shouldReturnFalse_whenUserDoesNotExist() {
        Long userId = 4L;

        when(restTemplate.getForObject(
                AUTH_SERVICE_URL + "/api/internal/users/" + userId,
                UserResponse.class
        )).thenThrow(new RuntimeException("Not found"));

        boolean exists = userServiceClient.userExists(userId);

        assertFalse(exists);
    }
}
