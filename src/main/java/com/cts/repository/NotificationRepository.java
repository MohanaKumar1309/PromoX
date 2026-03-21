package com.cts.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetUserIdOrderByCreatedAtDesc(Long userId);
}
