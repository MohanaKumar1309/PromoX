package com.cts.service;

import java.util.List;

import com.cts.dto.AuditLogGetDto;
import com.cts.entity.AuditLog;
import com.cts.entity.InternalUser;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.AuditLogRepository;
import com.cts.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final InternalUserRepository internalUserRepository;

    public void logAction(Long userId, String action, String metadata) {
        InternalUser user = internalUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Internal user not found"));

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setMetadata(metadata);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogGetDto> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream().map(this::toDto).toList();
    }

    private AuditLogGetDto toDto(AuditLog auditLog) {
        return AuditLogGetDto.builder()
                .auditId(auditLog.getAuditId())
                .userId(auditLog.getUser() != null ? auditLog.getUser().getUserId() : null)
                .userEmail(auditLog.getUser() != null ? auditLog.getUser().getEmail() : null)
                .userName(auditLog.getUser() != null ? auditLog.getUser().getName() : null)
                .action(auditLog.getAction())
                .metadata(auditLog.getMetadata())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
