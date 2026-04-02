package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.user.CreateUserRequest;
import com.example.finance_dashboard_backend.dto.user.UserResponse;
import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final AppUserRepository userRepository;

    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        AppUser user = new AppUser();
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setActive(request.active);

        AppUser saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // MAPPER METHOD
    private UserResponse mapToResponse(AppUser user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.username = user.getUsername();
        response.email = user.getEmail();
        return response;
    }
}
