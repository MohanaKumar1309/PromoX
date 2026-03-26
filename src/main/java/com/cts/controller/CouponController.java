package com.cts.controller;

import com.cts.common.ApiResponse;
import com.cts.dto.CouponGetDto;
import com.cts.dto.CreateCouponRequest;
import com.cts.enums.ApprovalStatus;
import com.cts.security.AuthContextService;
import com.cts.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final AuthContextService authContextService;

    @PreAuthorize("hasRole('MARKETING_MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<CouponGetDto>> create(@Valid @RequestBody CreateCouponRequest request,
                                                            Authentication authentication) {
        Long createdBy = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<CouponGetDto>builder()
                .success(true)
                .message("Coupon created and sent for approval")
                .data(couponService.create(request, createdBy))
                .build());
    }

    @PreAuthorize("hasRole('STORE_MANAGER')")
    @PatchMapping("/{couponId}/status")
    public ResponseEntity<ApiResponse<CouponGetDto>> updateStatus(@PathVariable Long couponId,
                                                                  @RequestParam ApprovalStatus status,
                                                                  Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<CouponGetDto>builder()
                .success(true)
                .message("Coupon status updated")
                .data(couponService.approveOrReject(couponId, status, actorUserId))
                .build());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CouponGetDto>>> active() {
        return ResponseEntity.ok(ApiResponse.<List<CouponGetDto>>builder()
                .success(true)
                .message("Active coupons")
                .data(couponService.activeCoupons())
                .build());
    }

    @PreAuthorize("hasAnyRole('MARKETING_MANAGER','STORE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponGetDto>>> all() {
        return ResponseEntity.ok(ApiResponse.<List<CouponGetDto>>builder()
                .success(true)
                .message("Coupons fetched")
                .data(couponService.all())
                .build());
    }

    @PreAuthorize("hasAnyRole('MARKETING_MANAGER','STORE_MANAGER')")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long couponId, Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        couponService.delete(couponId, actorUserId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Coupon deleted")
                .data(null)
                .build());
    }
}
