package org.mcintyrelab.service;

import org.mcintyrelab.dto.CreateUser;
import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(CreateUser createUser) {
        String username = createUser.username();
        String password = createUser.password();

        if(username == null || password == null){
            throw new RuntimeException("Username or Password cannot be null");
        }

        User createdUser = User.builder().
                username(username).
                password(password).
                build();

        if(userRepository.existsByUsername(username)){
            throw new RuntimeException("User already exists");
        }
        userRepository.save(createdUser);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
