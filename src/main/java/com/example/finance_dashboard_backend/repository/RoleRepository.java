package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.Role;
import com.example.finance_dashboard_backend.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
