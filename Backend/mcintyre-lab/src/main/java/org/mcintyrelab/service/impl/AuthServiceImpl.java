package org.mcintyrelab.service.impl;

import org.mcintyrelab.dto.auth.request.LoginRequest;
import org.mcintyrelab.dto.auth.response.LoginResponse;
import org.mcintyrelab.dto.auth.request.RegisterRequest;
import org.mcintyrelab.dto.auth.response.RegisterResponse;
import org.mcintyrelab.model.enums.Role;
import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UserRepository;
import org.mcintyrelab.security.JwtUtil;
import org.mcintyrelab.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new LoginResponse(jwtUtil.generateToken(userDetails.getUsername()));
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("Username already exists");
        }
        User newUser = User.builder()
                .firstName(registerRequest.firstname())
                .lastName(registerRequest.lastname())
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Role.ROLE_RESEARCHER) // Default role for now
                .build();
        userRepository.save(newUser);
        return new RegisterResponse(jwtUtil.generateToken(newUser.getUsername()));
    }
}
