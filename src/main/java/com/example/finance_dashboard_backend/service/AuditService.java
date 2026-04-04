package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.model.AuditActionType;
import com.example.finance_dashboard_backend.model.AuditLog;
import com.example.finance_dashboard_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(AuditActionType actionType, String entityType, String entityId, AppUser actor, String details) {
        auditLogRepository.save(AuditLog.builder().actionType(actionType).entityType(entityType).entityId(entityId).actor(actor).details(details).build());
    }
}
