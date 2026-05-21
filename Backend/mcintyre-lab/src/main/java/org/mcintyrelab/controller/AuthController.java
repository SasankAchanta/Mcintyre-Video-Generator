package org.mcintyrelab.controller;

import org.mcintyrelab.dto.auth.request.LoginRequest;
import org.mcintyrelab.dto.auth.response.LoginResponse;
import org.mcintyrelab.dto.auth.request.RegisterRequest;
import org.mcintyrelab.dto.auth.response.RegisterResponse;
import org.mcintyrelab.service.impl.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcintyre-lab/v1/auth")
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authServiceImpl.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authServiceImpl.register(registerRequest));
    }
}