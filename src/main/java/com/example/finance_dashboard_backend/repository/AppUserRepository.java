package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {
}
