package com.cts.dto;




import java.time.LocalDateTime;

import com.cts.enums.NotificationStatus;
import com.cts.enums.ReferenceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationGetDto {
    private Long notificationId;
    private String message;
    private NotificationStatus status;
    private ReferenceType category;
    private Long createdBy;
    private Long targetUserId;
    private LocalDateTime createdAt;
}
