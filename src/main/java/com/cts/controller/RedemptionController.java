package com.cts.controller;


import com.cts.service.RedemptionService;
import com.cts.common.ApiResponse;
import com.cts.dto.CheckoutRequest;
import com.cts.dto.CheckoutResponse;
import com.cts.security.AuthContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/redemptions")
@RequiredArgsConstructor
public class RedemptionController {

    private final RedemptionService redemptionService;
    private final AuthContextService authContextService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(@Valid @RequestBody CheckoutRequest request,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.<CheckoutResponse>builder()
                .success(true)
                .message("Checkout successful")
                .data(redemptionService.checkout(authContextService.currentCustomer(authentication), request))
                .build());
    }
}

