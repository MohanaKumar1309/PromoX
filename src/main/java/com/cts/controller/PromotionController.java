package com.cts.controller;

import com.cts.common.ApiResponse;
import com.cts.dto.CreatePromotionRequest;
import com.cts.dto.PromotionGetDto;
import com.cts.enums.ApprovalStatus;
import com.cts.security.AuthContextService;
import com.cts.service.PromotionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web .bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final AuthContextService authContextService;

    @PreAuthorize("hasRole('MERCHANDISER')")
    @PostMapping
    public ResponseEntity<ApiResponse<PromotionGetDto>> create(@Valid @RequestBody CreatePromotionRequest request,
                                                               Authentication authentication) {
        Long createdBy = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<PromotionGetDto>builder()
                .success(true)
                .message("Promotion created and sent for approval")
                .data(promotionService.create(request, createdBy))
                .build());
    }

    @PreAuthorize("hasRole('STORE_MANAGER')")
    @PatchMapping("/{promotionId}/status")
    public ResponseEntity<ApiResponse<PromotionGetDto>> updateStatus(@PathVariable Long promotionId,
                                                                     @RequestParam ApprovalStatus status,
                                                                     Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<PromotionGetDto>builder()
                .success(true)
                .message("Promotion status updated")
                .data(promotionService.approveOrReject(promotionId, status, actorUserId))
                .build());
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromotionGetDto>>> active() {
        return ResponseEntity.ok(ApiResponse.<List<PromotionGetDto>>builder()
                .success(true)
                .message("Active promotions")
                .data(promotionService.activePromotions())
                .build());
    }

    @PreAuthorize("hasAnyRole('MERCHANDISER','STORE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionGetDto>>> all() {
        return ResponseEntity.ok(ApiResponse.<List<PromotionGetDto>>builder()
                .success(true)
                .message("Promotions fetched")
                .data(promotionService.all())
                .build());
    }

    @PreAuthorize("hasAnyRole('MERCHANDISER','STORE_MANAGER')")
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long promotionId, Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        promotionService.delete(promotionId, actorUserId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Promotion deleted")
                .data(null)
                .build());
    }
}
