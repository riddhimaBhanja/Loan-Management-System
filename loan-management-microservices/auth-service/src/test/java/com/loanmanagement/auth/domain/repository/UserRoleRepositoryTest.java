package com.loanmanagement.auth.domain.repository;

import com.loanmanagement.auth.domain.model.User;

import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.auth.domain.model.RoleType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@test.com");
        user.setPasswordHash("hash");
        user.setFullName("Test User");
        user.setPhoneNumber("9999999999");
        user.setIsActive(true);
        user = userRepository.save(user);
    }

    private UserRole saveRole(RoleType roleType) {
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRole(roleType);
        return userRoleRepository.save(role);
    }

    @Test
    void shouldFindByUserId() {
        saveRole(RoleType.CUSTOMER);
        saveRole(RoleType.LOAN_OFFICER);

        List<UserRole> roles =
                userRoleRepository.findByUserId(user.getId());

        assertThat(roles).hasSize(2);
    }

    @Test
    void shouldFindByUserIdAndRole() {
        saveRole(RoleType.ADMIN);

        Optional<UserRole> role =
                userRoleRepository.findByUserIdAndRole(
                        user.getId(), RoleType.ADMIN);

        assertThat(role).isPresent();
    }

    @Test
    void shouldCheckExistsByUserIdAndRole() {
        saveRole(RoleType.CUSTOMER);

        boolean exists =
                userRoleRepository.existsByUserIdAndRole(
                        user.getId(), RoleType.CUSTOMER);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteByUserId() {
        saveRole(RoleType.CUSTOMER);
        saveRole(RoleType.ADMIN);

        userRoleRepository.deleteByUserId(user.getId());

        List<UserRole> remaining =
                userRoleRepository.findByUserId(user.getId());

        assertThat(remaining).isEmpty();
    }

    @Test
    void shouldCountUsersByRole() {
        saveRole(RoleType.CUSTOMER);

        Long count =
                userRoleRepository.countByRole(RoleType.CUSTOMER);

        assertThat(count).isEqualTo(1);
    }
}
