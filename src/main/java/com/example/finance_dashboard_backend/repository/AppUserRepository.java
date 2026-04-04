package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.model.BusinessUnit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    @EntityGraph(attributePaths = {"role", "role.permissions", "businessUnit"})
    Optional<AppUser> findByUsername(String username);

    @EntityGraph(attributePaths = {"role", "role.permissions", "businessUnit"})
    Optional<AppUser> findById(Long id);

    Optional<AppUser> findByEmailIgnoreCase(String email);

    List<AppUser> findByBusinessUnit(BusinessUnit businessUnit);
}
