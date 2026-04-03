package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.Permission;
import com.example.finance_dashboard_backend.model.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionType name);
}
