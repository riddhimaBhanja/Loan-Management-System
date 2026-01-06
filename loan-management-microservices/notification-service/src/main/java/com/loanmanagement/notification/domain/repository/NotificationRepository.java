package com.loanmanagement.notification.domain.repository;

import com.loanmanagement.notification.domain.model.Notification;
import com.loanmanagement.notification.domain.model.NotificationStatus;
import com.loanmanagement.notification.domain.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications by recipient email
     */
    Page<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail, Pageable pageable);

    /**
     * Find all notifications by type
     */
    Page<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type, Pageable pageable);

    /**
     * Find all notifications by status
     */
    Page<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status, Pageable pageable);

    /**
     * Find all notifications by user ID
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find all notifications by loan application ID
     */
    List<Notification> findByLoanApplicationIdOrderByCreatedAtDesc(Long loanApplicationId);

    /**
     * Find all notifications by EMI ID
     */
    List<Notification> findByEmiIdOrderByCreatedAtDesc(Long emiId);

    /**
     * Find pending notifications for retry
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.retryCount < :maxRetries ORDER BY n.createdAt ASC")
    List<Notification> findPendingNotificationsForRetry(@Param("maxRetries") int maxRetries);

    /**
     * Find failed notifications
     */
    List<Notification> findByStatusAndRetryCountLessThanOrderByCreatedAtDesc(
            NotificationStatus status, int maxRetries);

    /**
     * Count notifications by status
     */
    long countByStatus(NotificationStatus status);

    /**
     * Count notifications by type and date range
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.type = :type AND n.createdAt BETWEEN :startDate AND :endDate")
    long countByTypeAndDateRange(
            @Param("type") NotificationType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find recent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("since") LocalDateTime since);
}
