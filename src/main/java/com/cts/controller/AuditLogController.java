package com.cts.controller;

import java.util.List;

import com.cts.common.ApiResponse;
import com.cts.dto.AuditLogGetDto;
import com.cts.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogGetDto>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.<List<AuditLogGetDto>>builder()
                .success(true)
                .message("Audit logs fetched")
                .data(auditLogService.getAll())
                .build());
    }
}
