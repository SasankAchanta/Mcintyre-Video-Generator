package org.mcintyrelab.service;

import org.mcintyrelab.dto.auth.LoginRequest;
import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
