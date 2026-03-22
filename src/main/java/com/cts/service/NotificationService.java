package com.cts.service;


import com.cts.dto.NotificationGetDto;
import com.cts.entity.InternalUser;
import com.cts.entity.Notification;
import com.cts.enums.NotificationStatus;
import com.cts.enums.ReferenceType;
import com.cts.enums.Role;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.InternalUserRepository;
import com.cts.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final InternalUserRepository internalUserRepository;

    public void notifyStoreManagers(String message, ReferenceType referenceType, Long createdBy) {
        List<InternalUser> storeManagers = internalUserRepository.findByRole(Role.STORE_MANAGER);
        List<Notification> notifications = new ArrayList<>();
        for (InternalUser manager : storeManagers) {
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setCategory(referenceType);
            notification.setCreatedBy(createdBy);
            notification.setTargetUserId(manager.getUserId());
            notifications.add(notification);
        }
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    public List<NotificationGetDto> getForUser(Long userId) {
        return notificationRepository.findByTargetUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public NotificationGetDto markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setStatus(NotificationStatus.READ);
        return toDto(notificationRepository.save(notification));
    }

    private NotificationGetDto toDto(Notification notification) {
        return NotificationGetDto.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .category(notification.getCategory())
                .createdBy(notification.getCreatedBy())
                .targetUserId(notification.getTargetUserId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
