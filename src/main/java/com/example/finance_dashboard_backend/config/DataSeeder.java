package com.example.finance_dashboard_backend.config;


import com.example.finance_dashboard_backend.model.*;
import com.example.finance_dashboard_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {
    private final AppUserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        seedPermissions();
        seedRoles();

        BusinessUnit financeOps = businessUnitRepository.save(BusinessUnit.builder().code("FIN-OPS").name("Finance Operations").build());
        BusinessUnit growth = businessUnitRepository.save(BusinessUnit.builder().code("GROWTH").name("Growth and Revenue").build());

        AppUser viewer = userRepository.save(buildUser("viewer", "Finance Viewer", "viewer@example.com", "viewer123", RoleType.VIEWER, financeOps));
        AppUser analyst = userRepository.save(buildUser("analyst", "Finance Analyst", "analyst@example.com", "analyst123", RoleType.ANALYST, financeOps));
        AppUser admin = userRepository.save(buildUser("admin", "Finance Admin", "admin@example.com", "admin123", RoleType.ADMIN, financeOps));
        AppUser growthAnalyst = userRepository.save(buildUser("growth_analyst", "Growth Analyst", "growth@example.com", "growth123", RoleType.ANALYST, growth));

        seedRecord(admin, financeOps, "12000.00", RecordType.INCOME, "Consulting", LocalDate.of(2026, 1, 10), "January consulting revenue");
        seedRecord(admin, financeOps, "3200.00", RecordType.EXPENSE, "Operations", LocalDate.of(2026, 1, 15), "Software and tooling");
        seedRecord(analyst, financeOps, "9800.00", RecordType.INCOME, "Subscriptions", LocalDate.of(2026, 2, 3), "Monthly subscription billing");
        seedRecord(admin, financeOps, "4100.00", RecordType.EXPENSE, "Payroll", LocalDate.of(2026, 2, 20), "Contractor payments");
        seedRecord(admin, financeOps, "15000.00", RecordType.INCOME, "Enterprise", LocalDate.of(2026, 3, 12), "Enterprise onboarding package");
        seedRecord(admin, financeOps, "2750.00", RecordType.EXPENSE, "Marketing", LocalDate.of(2026, 3, 21), "Campaign creative and ads");
        seedRecord(growthAnalyst, growth, "6300.00", RecordType.INCOME, "Affiliate", LocalDate.of(2026, 3, 5), "Affiliate channel revenue");
        seedRecord(growthAnalyst, growth, "1900.00", RecordType.EXPENSE, "Acquisition", LocalDate.of(2026, 3, 8), "Paid search acquisition spend");
    }

    private void seedPermissions() {
        for (PermissionType type : PermissionType.values()) {
            permissionRepository.save(Permission.builder()
                    .name(type)
                    .description(type.name().replace('_', ' '))
                    .build());
        }
    }

    private void seedRoles() {
        roleRepository.save(Role.builder().name(RoleType.VIEWER).permissions(resolvePermissions(EnumSet.of(
                PermissionType.DASHBOARD_READ,
                PermissionType.SNAPSHOT_READ
        ))).build());
        roleRepository.save(Role.builder().name(RoleType.ANALYST).permissions(resolvePermissions(EnumSet.of(
                PermissionType.DASHBOARD_READ,
                PermissionType.RECORD_READ,
                PermissionType.EXPORT_RECORDS,
                PermissionType.SNAPSHOT_READ
        ))).build());
        roleRepository.save(Role.builder().name(RoleType.ADMIN).permissions(resolvePermissions(EnumSet.allOf(PermissionType.class))).build());
    }

    private Set<Permission> resolvePermissions(Set<PermissionType> types) {
        return types.stream()
                .map(type -> permissionRepository.findByName(type).orElseThrow())
                .collect(java.util.stream.Collectors.toSet());
    }

    private AppUser buildUser(String username, String fullName, String email, String rawPassword, RoleType roleType, BusinessUnit businessUnit) {
        return AppUser.builder()
                .username(username)
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(roleRepository.findByName(roleType).orElseThrow())
                .status(UserStatus.ACTIVE)
                .businessUnit(businessUnit)
                .build();
    }

    private void seedRecord(AppUser creator, BusinessUnit businessUnit, String amount, RecordType type, String category, LocalDate entryDate, String notes) {
        financialRecordRepository.save(FinancialRecord.builder()
                .amount(new BigDecimal(amount))
                .type(type)
                .category(category)
                .entryDate(entryDate)
                .notes(notes)
                .businessUnit(businessUnit)
                .createdBy(creator)
                .updatedBy(creator)
                .deleted(false)
                .build());
    }
}
