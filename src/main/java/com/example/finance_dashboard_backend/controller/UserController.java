package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.dto.user.CreateUserRequest;
import com.example.finance_dashboard_backend.dto.user.UserResponse;
import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}
