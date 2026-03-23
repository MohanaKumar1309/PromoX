package com.cts.dto;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogGetDto {
    private Long auditId;
    private Long userId;
    private String userEmail;
    private String userName;
    private String action;
    private String metadata;
    private LocalDateTime timestamp;
}