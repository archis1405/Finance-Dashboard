package com.example.finance_dashboard_backend.security;

import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.model.Permission;
import com.example.finance_dashboard_backend.model.PermissionType;
import com.example.finance_dashboard_backend.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.finance_dashboard_backend.model.RoleType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class SecurityUser implements UserDetails {
    private final AppUser user;

    public SecurityUser(AppUser user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public RoleType getRole() {
        return user.getRole().getName();
    }

    public UserStatus getStatus() {
        return user.getStatus();
    }

    public String getBusinessUnitCode() {
        return user.getBusinessUnit().getCode();
    }

    public Set<String> getPermissionNames() {
        return user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .map(PermissionType::name)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean hasPermission(PermissionType permissionType) {
        return getPermissionNames().contains(permissionType.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));
        for (Permission permission : user.getRole().getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(permission.getName().name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
