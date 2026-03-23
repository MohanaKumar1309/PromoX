package com.cts.controller;


import com.cts.common.ApiResponse;
import com.cts.dto.AnalyticsSummaryGetDto;
import com.cts.service.AnalyticsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PreAuthorize("hasRole('STORE_MANAGER')")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<AnalyticsSummaryGetDto>>> summary() {
        return ResponseEntity.ok(ApiResponse.<List<AnalyticsSummaryGetDto>>builder()
                .success(true)
                .message("Analytics summary fetched")
                .data(analyticsService.summary())
                .build());
    }
}
