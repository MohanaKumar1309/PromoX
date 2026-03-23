package com.cts.controller;



import com.cts.common.ApiResponse;
import com.cts.dto.NotificationGetDto;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.InternalUserRepository;
import com.cts.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final InternalUserRepository internalUserRepository;

    @PreAuthorize("hasAnyRole('STORE_MANAGER','MERCHANDISER','MARKETING_MANAGER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<NotificationGetDto>>> mine(Authentication authentication) {
        Long userId = internalUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Internal user not found"))
                .getUserId();

        return ResponseEntity.ok(ApiResponse.<List<NotificationGetDto>>builder()
                .success(true)
                .message("Notifications fetched")
                .data(notificationService.getForUser(userId))
                .build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationGetDto>> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<NotificationGetDto>builder()
                .success(true)
                .message("Notification marked as read")
                .data(notificationService.markAsRead(id))
                .build());
    }
}
