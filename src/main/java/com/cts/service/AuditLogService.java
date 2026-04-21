package com.cts.service;

import java.util.List;

import com.cts.dto.AuditLogGetDto;
import com.cts.entity.AuditLog;
import com.cts.repository.AuditLogRepository;
import com.cts.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final InternalUserRepository internalUserRepository;

    public void logAction(Long userId, String action, String metadata) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        internalUserRepository.findById(userId).ifPresent(user -> {
            auditLog.setUserName(user.getName());
            auditLog.setUserEmail(user.getEmail());
        });
        auditLog.setAction(action);
        auditLog.setMetadata(metadata);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogGetDto> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream().map(this::toDto).toList();
    }

    public Page<AuditLogGetDto> getPage(int page, int size) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size))
                .map(this::toDto);
    }

    private AuditLogGetDto toDto(AuditLog auditLog) {
        return AuditLogGetDto.builder()
                .auditId(auditLog.getAuditId())
                .userId(auditLog.getUserId())
                .userEmail(auditLog.getUserEmail())
                .userName(auditLog.getUserName())
                .action(auditLog.getAction())
                .metadata(auditLog.getMetadata())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
