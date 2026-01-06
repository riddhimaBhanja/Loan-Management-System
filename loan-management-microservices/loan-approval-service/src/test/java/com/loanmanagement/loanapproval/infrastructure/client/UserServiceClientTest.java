package com.loanmanagement.loanapproval.infrastructure.client;

import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceClient userServiceClient;

    private final String authServiceUrl = "http://auth-service";

    @BeforeEach
    void setUp() {
        userServiceClient = new UserServiceClient(restTemplate, authServiceUrl);
    }

    @Test
    void getUserById_success() {
        Long userId = 1L;

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(
                new HashSet<>(Arrays.asList("ADMIN", "USER"))
        );

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        UserDetailsDTO result = userServiceClient.getUserById(userId);

        assertNotNull(result);
        assertEquals(userDetailsDTO, result);
    }

    @Test
    void getUserById_notFound_throwsException() {
        Long userId = 2L;

        when(restTemplate.getForEntity(anyString(), eq(UserDetailsDTO.class)))
                .thenThrow(new RuntimeException("404"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceClient.getUserById(userId)
        );
    }

    @Test
    void userHasRole_returnsTrue() {
        Long userId = 3L;
        String roleName = "ADMIN";

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(
                new HashSet<>(Arrays.asList("ADMIN", "USER"))
        );

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        boolean result = userServiceClient.userHasRole(userId, roleName);

        assertTrue(result);
    }

    @Test
    void userHasRole_returnsFalse_whenRoleMissing() {
        Long userId = 4L;
        String roleName = "LOAN_OFFICER";

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(
                Collections.singleton("USER")
        );

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        boolean result = userServiceClient.userHasRole(userId, roleName);

        assertFalse(result);
    }

    @Test
    void userHasRole_returnsFalse_whenRolesNull() {
        Long userId = 5L;

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(null);

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        boolean result = userServiceClient.userHasRole(userId, "ADMIN");

        assertFalse(result);
    }

    @Test
    void userHasAnyRole_returnsTrue() {
        Long userId = 6L;
        List<String> roleNames = Arrays.asList("ADMIN", "LOAN_OFFICER");

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(
                new HashSet<>(Arrays.asList("USER", "ADMIN"))
        );

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        boolean result = userServiceClient.userHasAnyRole(userId, roleNames);

        assertTrue(result);
    }

    @Test
    void userHasAnyRole_returnsFalse() {
        Long userId = 7L;
        List<String> roleNames = Arrays.asList("ADMIN", "LOAN_OFFICER");

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(
                Collections.singleton("USER")
        );

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        boolean result = userServiceClient.userHasAnyRole(userId, roleNames);

        assertFalse(result);
    }

    @Test
    void userHasAnyRole_returnsFalse_whenRolesNull() {
        Long userId = 8L;
        List<String> roleNames = Arrays.asList("ADMIN", "USER");

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRoles(null);

        ResponseEntity<UserDetailsDTO> response =
                new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                authServiceUrl + "/api/internal/users/" + userId,
                UserDetailsDTO.class
        )).thenReturn(response);

        boolean result = userServiceClient.userHasAnyRole(userId, roleNames);

        assertFalse(result);
    }
}
