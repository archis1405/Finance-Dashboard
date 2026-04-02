package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final AppUserRepository userRepository;

    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser createUser(AppUser user) {
        return userRepository.save(user);
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}
