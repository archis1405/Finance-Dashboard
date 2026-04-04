package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long> {

    Optional<BusinessUnit> findByCodeIgnoreCase(String code);

}