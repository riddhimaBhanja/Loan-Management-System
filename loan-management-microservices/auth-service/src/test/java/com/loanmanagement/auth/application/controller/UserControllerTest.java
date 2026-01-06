package com.loanmanagement.auth.application.controller;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRolesRequest;
import com.loanmanagement.auth.application.dto.response.PageResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.service.UserService;
import com.loanmanagement.auth.shared.constants.ApiConstants;
import com.loanmanagement.auth.shared.constants.MessageConstants;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for UserController
 * Uses @WebMvcTest for controller layer testing
 * Target Coverage: 95%+
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UpdateUserRolesRequest updateUserRolesRequest;
    private PageResponse<UserResponse> pageResponse;

    @BeforeEach
    void setUp() {
        // Setup user response
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setFullName("Test User");
        userResponse.setPhoneNumber("+1234567890");
        userResponse.setIsActive(true);
        userResponse.setRoles(Set.of(RoleType.CUSTOMER));

        // Setup create user request
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newuser");
        createUserRequest.setEmail("newuser@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setFullName("New User");
        createUserRequest.setPhoneNumber("+9876543210");
        createUserRequest.setRoles(Set.of(RoleType.ADMIN));

        // Setup update user request
        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setFullName("Updated Name");
        updateUserRequest.setPhoneNumber("+1111111111");

        // Setup update user roles request
        updateUserRolesRequest = new UpdateUserRolesRequest();
        updateUserRolesRequest.setRoles(Set.of(RoleType.ADMIN, RoleType.LOAN_OFFICER));

        // Setup page response
        pageResponse = new PageResponse<>();
        pageResponse.setContent(List.of(userResponse));
        pageResponse.setCurrentPage(0);
        pageResponse.setTotalPages(1);
        pageResponse.setTotalElements(1L);
        pageResponse.setSize(10);
    }

    // ===================== GET ALL USERS =====================

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnOk_WhenUsersExist() throws Exception {
        // Given
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pageResponse);

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    @DisplayName("Should get all users with default pagination")
    void getAllUsers_ShouldUseDefaults_WhenNoParamsProvided() throws Exception {
        // Given
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pageResponse);

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    @DisplayName("Should sort users ascending when sortDir is asc")
    void getAllUsers_ShouldSortAscending_WhenSortDirIsAsc() throws Exception {
        // Given
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pageResponse);

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH)
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(any(Pageable.class));
    }

    // ===================== GET USER BY ID =====================

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_ShouldReturnOk_WhenUserExists() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void getUserById_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Given
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH + "/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    // ===================== GET CURRENT USER =====================

    @Test
    @DisplayName("Should get current user successfully")
    void getCurrentUser_ShouldReturnOk_WhenAuthenticated() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(userResponse);

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH + "/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getCurrentUser();
    }

    // ===================== CREATE USER =====================

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ShouldReturnCreated_WhenValidRequest() throws Exception {
        // Given
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        // When / Then
        mockMvc.perform(post(ApiConstants.USER_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.USER_CREATED))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should return error when creating user with existing username")
    void createUser_ShouldReturnBadRequest_WhenUsernameExists() throws Exception {
        // Given
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new BusinessException(MessageConstants.USER_ALREADY_EXISTS));

        // When / Then
        mockMvc.perform(post(ApiConstants.USER_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should return validation error when create request is invalid")
    void createUser_ShouldReturnBadRequest_WhenRequestInvalid() throws Exception {
        // Given - Invalid request (empty username)
        createUserRequest.setUsername("");

        // When / Then
        mockMvc.perform(post(ApiConstants.USER_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    // ===================== UPDATE USER =====================

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_ShouldReturnOk_WhenValidRequest() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(userResponse);

        // When / Then
        mockMvc.perform(put(ApiConstants.USER_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.USER_UPDATED))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent user")
    void updateUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Given
        when(userService.updateUser(eq(999L), any(UpdateUserRequest.class)))
                .thenThrow(new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));

        // When / Then
        mockMvc.perform(put(ApiConstants.USER_BASE_PATH + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("Should return error when updating email to existing one")
    void updateUser_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenThrow(new BusinessException("Email already in use"));

        // When / Then
        mockMvc.perform(put(ApiConstants.USER_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    // ===================== DEACTIVATE USER =====================

    @Test
    @DisplayName("Should deactivate user successfully")
    void deactivateUser_ShouldReturnOk_WhenUserExists() throws Exception {
        // Given
        doNothing().when(userService).deactivateUser(1L);

        // When / Then
        mockMvc.perform(delete(ApiConstants.USER_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.USER_DELETED));

        verify(userService).deactivateUser(1L);
    }

    @Test
    @DisplayName("Should return not found when deactivating non-existent user")
    void deactivateUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND))
                .when(userService).deactivateUser(999L);

        // When / Then
        mockMvc.perform(delete(ApiConstants.USER_BASE_PATH + "/999"))
                .andExpect(status().isNotFound());

        verify(userService).deactivateUser(999L);
    }

    // ===================== ACTIVATE USER =====================

    @Test
    @DisplayName("Should activate user successfully")
    void activateUser_ShouldReturnOk_WhenUserExists() throws Exception {
        // Given
        doNothing().when(userService).activateUser(1L);

        // When / Then
        mockMvc.perform(patch(ApiConstants.USER_BASE_PATH + "/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User activated successfully"));

        verify(userService).activateUser(1L);
    }

    @Test
    @DisplayName("Should return not found when activating non-existent user")
    void activateUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND))
                .when(userService).activateUser(999L);

        // When / Then
        mockMvc.perform(patch(ApiConstants.USER_BASE_PATH + "/999/activate"))
                .andExpect(status().isNotFound());

        verify(userService).activateUser(999L);
    }

    // ===================== UPDATE USER ROLES =====================

    @Test
    @DisplayName("Should update user roles successfully")
    void updateUserRoles_ShouldReturnOk_WhenValidRequest() throws Exception {
        // Given
        when(userService.updateUserRoles(eq(1L), any(UpdateUserRolesRequest.class)))
                .thenReturn(userResponse);

        // When / Then
        mockMvc.perform(put(ApiConstants.USER_BASE_PATH + "/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRolesRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User roles updated successfully"))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).updateUserRoles(eq(1L), any(UpdateUserRolesRequest.class));
    }

    @Test
    @DisplayName("Should return not found when updating roles for non-existent user")
    void updateUserRoles_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Given
        when(userService.updateUserRoles(eq(999L), any(UpdateUserRolesRequest.class)))
                .thenThrow(new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));

        // When / Then
        mockMvc.perform(put(ApiConstants.USER_BASE_PATH + "/999/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRolesRequest)))
                .andExpect(status().isNotFound());

        verify(userService).updateUserRoles(eq(999L), any(UpdateUserRolesRequest.class));
    }

    @Test
    @DisplayName("Should return validation error when roles request is invalid")
    void updateUserRoles_ShouldReturnBadRequest_WhenRequestInvalid() throws Exception {
        // Given - Invalid request (empty roles)
        updateUserRolesRequest.setRoles(Set.of());

        // When / Then
        mockMvc.perform(put(ApiConstants.USER_BASE_PATH + "/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRolesRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUserRoles(anyLong(), any());
    }

    // ===================== GET ALL ROLES =====================

    @Test
    @DisplayName("Should get all roles successfully")
    void getAllRoles_ShouldReturnOk_WhenCalled() throws Exception {
        // Given
        List<RoleType> roles = Arrays.asList(RoleType.values());
        when(userService.getAllRoles()).thenReturn(roles);

        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH + "/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(roles.size()));

        verify(userService).getAllRoles();
    }

    // ===================== PATH VARIABLE VALIDATION =====================

    @Test
    @DisplayName("Should return bad request for invalid user ID format")
    void getUserById_ShouldReturnBadRequest_WhenIdFormatInvalid() throws Exception {
        // When / Then
        mockMvc.perform(get(ApiConstants.USER_BASE_PATH + "/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
    }

    // ===================== CONTENT TYPE VALIDATION =====================

    @Test
    @DisplayName("Should reject request with wrong content type")
    void createUser_ShouldReturnUnsupportedMediaType_WhenWrongContentType() throws Exception {
        // When / Then
        mockMvc.perform(post(ApiConstants.USER_BASE_PATH)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isUnsupportedMediaType());

        verify(userService, never()).createUser(any());
    }

    // ===================== ENDPOINT PATH TESTS =====================

    @Test
    @DisplayName("Should handle user endpoints with correct base path")
    void endpoints_ShouldUseCorrectBasePath() throws Exception {
        // Given
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pageResponse);

        // When / Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(any(Pageable.class));
    }
}
