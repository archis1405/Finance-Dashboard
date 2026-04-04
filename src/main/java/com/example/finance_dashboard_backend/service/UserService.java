package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.auth.AuthUserResponse;
import com.example.finance_dashboard_backend.dto.user.CreateUserRequest;
import com.example.finance_dashboard_backend.dto.user.UpdateUserRequest;
import com.example.finance_dashboard_backend.dto.user.UserResponse;
import com.example.finance_dashboard_backend.exception.BadRequestException;
import com.example.finance_dashboard_backend.exception.ResourceNotFoundException;
import com.example.finance_dashboard_backend.model.*;
import com.example.finance_dashboard_backend.repository.AppUserRepository;
import com.example.finance_dashboard_backend.repository.BusinessUnitRepository;
import com.example.finance_dashboard_backend.repository.RoleRepository;
import com.example.finance_dashboard_backend.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public UserResponse create(CreateUserRequest request, SecurityUser actor) {
        validateUniqueFields(request.username(), request.email(), null);
        Role role = getRole(request.role());
        BusinessUnit businessUnit = getBusinessUnit(request.businessUnitCode());

        AppUser user = AppUser.builder()
                .username(request.username().trim())
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .status(request.status())
                .businessUnit(businessUnit)
                .build();

        AppUser saved = userRepository.save(user);
        auditService.log(AuditActionType.CREATE, "USER", saved.getId().toString(), getActor(actor),
                "Created user " + saved.getUsername());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request, SecurityUser actor) {
        AppUser user = getUser(id);
        validateUniqueFields(user.getUsername(), request.email(), id);

        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setRole(getRole(request.role()));
        user.setStatus(request.status());
        user.setBusinessUnit(getBusinessUnit(request.businessUnitCode()));
        AppUser saved = userRepository.save(user);
        auditService.log(AuditActionType.UPDATE, "USER", saved.getId().toString(), getActor(actor),
                "Updated user " + saved.getUsername());
        return toResponse(saved);
    }

    @Transactional
    public UserResponse updateStatus(Long id, UserStatus status, SecurityUser actor) {
        AppUser user = getUser(id);
        user.setStatus(status);
        AppUser saved = userRepository.save(user);
        auditService.log(AuditActionType.STATUS_CHANGE, "USER", saved.getId().toString(), getActor(actor),
                "Changed status to " + status);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AuthUserResponse getAuthenticatedUser(SecurityUser principal) {
        AppUser user = getUser(principal.getId());
        return new AuthUserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                permissionNames(user.getRole()),
                user.getStatus(),
                user.getBusinessUnit().getCode());
    }

    @Transactional(readOnly = true)
    public AppUser getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Transactional(readOnly = true)
    public AppUser getActiveUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Inactive users cannot perform this action");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public BusinessUnit getBusinessUnit(String code) {
        return businessUnitRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Business unit not found: " + code));
    }

    private Role getRole(RoleType roleType) {
        return roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleType));
    }

    private AppUser getActor(SecurityUser actor) {
        return actor == null ? null : getActiveUserByUsername(actor.getUsername());
    }

    private void validateUniqueFields(String username, String email, Long userId) {
        userRepository.findByUsername(username.trim())
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BadRequestException("Username already exists");
                });
        userRepository.findByEmailIgnoreCase(email.trim())
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BadRequestException("Email already exists");
                });
    }

    private Set<String> permissionNames(Role role) {
        return role.getPermissions().stream().map(Permission::getName).map(Enum::name).collect(java.util.stream.Collectors.toSet());
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                permissionNames(user.getRole()),
                user.getStatus(),
                user.getBusinessUnit().getCode(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
