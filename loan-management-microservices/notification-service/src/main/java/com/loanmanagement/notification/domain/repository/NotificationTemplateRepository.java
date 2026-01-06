package com.loanmanagement.notification.domain.repository;

import com.loanmanagement.notification.domain.model.NotificationTemplate;
import com.loanmanagement.notification.domain.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for NotificationTemplate entity
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    /**
     * Find template by name
     */
    Optional<NotificationTemplate> findByName(String name);

    /**
     * Find template by type (returns the first active one)
     */
    Optional<NotificationTemplate> findFirstByTypeAndIsActiveTrue(NotificationType type);

    /**
     * Find all templates by type
     */
    List<NotificationTemplate> findByType(NotificationType type);

    /**
     * Find all active templates
     */
    List<NotificationTemplate> findByIsActiveTrueOrderByNameAsc();

    /**
     * Check if template exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all templates ordered by name
     */
    List<NotificationTemplate> findAllByOrderByNameAsc();
}
