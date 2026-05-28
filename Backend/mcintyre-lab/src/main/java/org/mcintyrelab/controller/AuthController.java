package org.mcintyrelab.controller;

import jakarta.validation.Valid;
import org.mcintyrelab.dto.auth.request.LoginRequest;
import org.mcintyrelab.dto.auth.request.VerifyEmailRequest;
import org.mcintyrelab.dto.auth.response.LoginResponse;
import org.mcintyrelab.dto.auth.request.RegisterRequest;
import org.mcintyrelab.dto.auth.response.RegisterResponse;
import org.mcintyrelab.service.AuthService; // Changed import to use the Interface
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcintyre-lab/v1/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
        authService.checkVerificationCode(verifyEmailRequest);
        return ResponseEntity.ok("Account successfully verified! You can now log in.");
    }

    // RESEND CODE ENDPOINT
    @PostMapping("/resend-code")
    public ResponseEntity<String> resendVerificationCode(@RequestParam String email) {
        // Calls the single helper method workflow through the service
        authService.resendVerificationCode(email);

        return ResponseEntity.ok("A fresh 6-digit verification code has been sent to your email.");
    }
}