package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.dto.user.CreateUserRequest;
import com.example.finance_dashboard_backend.dto.user.UpdateUserRequest;
import com.example.finance_dashboard_backend.dto.user.UpdateUserStatusRequest;
import com.example.finance_dashboard_backend.dto.user.UserResponse;
import com.example.finance_dashboard_backend.security.SecurityUser;
import com.example.finance_dashboard_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request, @AuthenticationPrincipal SecurityUser principal) {
        return userService.create(request, principal);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public List<UserResponse> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public UserResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return userService.update(id, request, principal);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public UserResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return userService.updateStatus(id, request.status(), principal);
    }
}
