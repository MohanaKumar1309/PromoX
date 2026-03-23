package com.cts.controller;

import com.cts.common.ApiResponse;
import com.cts.dto.CreateInternalUserRequest;
import com.cts.dto.UserGetDto;
import com.cts.security.AuthContextService;
import com.cts.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AuthService authService;
    private final AuthContextService authContextService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserGetDto>> createInternalUser(
            @Valid @RequestBody CreateInternalUserRequest request,
            Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<UserGetDto>builder()
                .success(true)
                .message("Internal user created")
                .data(authService.createInternalUser(request, actorUserId))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserGetDto>>> getInternalUsers() {
        return ResponseEntity.ok(ApiResponse.<List<UserGetDto>>builder()
                .success(true)
                .message("Internal users fetched")
                .data(authService.getAllInternalUsers())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteInternalUser(@PathVariable Long userId, Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        authService.deleteInternalUser(userId, actorUserId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Internal user deleted")
                .data(null)
                .build());
    }
}
