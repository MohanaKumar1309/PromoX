package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.cts.entity.Notification;
import com.cts.enums.NotificationStatus;
import com.cts.repository.InternalUserRepository;
import com.cts.repository.NotificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private InternalUserRepository internalUserRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void markAsRead_ShouldUpdateStatus() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setStatus(NotificationStatus.UNREAD);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var updated = notificationService.markAsRead(1L);

        assertEquals(NotificationStatus.READ, updated.getStatus());
    }
}