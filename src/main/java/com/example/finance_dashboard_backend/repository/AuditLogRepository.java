package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
