package org.mcintyrelab.service.impl;

import org.mcintyrelab.dto.auth.request.LoginRequest;
import org.mcintyrelab.dto.auth.request.VerifyEmailRequest;
import org.mcintyrelab.dto.auth.response.LoginResponse;
import org.mcintyrelab.dto.auth.request.RegisterRequest;
import org.mcintyrelab.dto.auth.response.RegisterResponse;
import org.mcintyrelab.model.enums.Role;
import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UserRepository;
import org.mcintyrelab.security.JwtUtil;
import org.mcintyrelab.service.AuthService;
import org.mcintyrelab.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // REUSABLE HELPER METHOD
    private void generateAndSendVerificationCode(User user) {
        // Generate the 6-digit PIN
        Integer verificationToken = generateVerificationCode();

        // Set the 15-minute expiration clock
        user.setVerificationToken(verificationToken);
        user.setTokenExpiryDate(java.time.LocalDateTime.now().plusMinutes(15));

        // Save those security details to the database
        userRepository.save(user);

        // Fire off the styled HTML email via Brevo
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }

    // REGISTER FLOW
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Email already exists");
        }

        // Build the base user entity.
        User newUser = User.builder()
                .firstName(registerRequest.firstname())
                .lastName(registerRequest.lastname())
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Role.ROLE_RESEARCHER) // Default role
                .isVerified(false) // Account locked until verification code is checked!
                .build();

        // 1. Save base user first so Hibernate tracks it
        userRepository.save(newUser);

        // 2. Use our centralized helper method to assign the token and email it!
        generateAndSendVerificationCode(newUser);

        return new RegisterResponse("Registration successful! Please check your email for your 6-digit verification code.");
    }

    // RESEND FLOW
    @Override
    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        if (user.isVerified()) {
            throw new RuntimeException("This account is already verified. Please log in.");
        }

        // Use our exact same centralized helper method to refresh the token and re-email it!
        generateAndSendVerificationCode(user);
    }

    // LOGIN GUARD FLOW
    @Override
    public LoginResponse login(LoginRequest request) {
        // Look up the user first to check their verified status
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Guard: Block them instantly if they haven't verified their email yet
        if (!user.isVerified()) {
            // Append the actual email from the database directly into the error message!
            throw new RuntimeException("UNVERIFIED:" + user.getEmail());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new LoginResponse(jwtUtil.generateToken(userDetails.getUsername()));
    }

    // VERIFY FLOW
    @Override
    @Transactional
    public Boolean checkVerificationCode(VerifyEmailRequest verifyEmailRequest) {
        User user = userRepository.findByEmail(verifyEmailRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + verifyEmailRequest.email()));

        // 2. If already verified, don't re-process
        if (user.isVerified()) {
            throw new RuntimeException("Account is already verified.");
        }

        // 3. Check if the code has expired
        if (user.getTokenExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired. Please request a new one.");
        }

        // 4. Check if the code matches what's in the database
        if (!user.getVerificationToken().equals(verifyEmailRequest.verificationToken())) {
            throw new RuntimeException("Invalid verification code.");
        }

        // 5. Code is perfect! Activate the user and clear the temporary security fields
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiryDate(null);

        userRepository.save(user);
        return true;
    }

    // CODE GENERATOR UTILITY
    @Override
    public Integer generateVerificationCode() {
        int min = 100000;
        int max = 999999;
        return (int)(Math.random() * (max - min + 1)) + min;
    }
}