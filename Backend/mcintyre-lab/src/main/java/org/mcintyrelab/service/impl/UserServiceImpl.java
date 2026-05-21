package org.mcintyrelab.service.impl;

import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.user.request.AllUsersRequest;
import org.mcintyrelab.dto.user.request.PasswordUpdateRequest;
import org.mcintyrelab.dto.user.request.UsernameUpdateRequest;
import org.mcintyrelab.model.User;
import org.mcintyrelab.model.enums.AuditAction;
import org.mcintyrelab.repository.UserRepository;
import org.mcintyrelab.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditServiceImpl auditServiceImpl;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditServiceImpl auditServiceImpl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditServiceImpl = auditServiceImpl;
    }

    @Transactional
    @Override
    public void updatePassword(String username, PasswordUpdateRequest passwordUpdateRequest) {
        // load user from DB
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // verify current password is correct
        if (!passwordEncoder.matches(passwordUpdateRequest.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update your compliance tracking timestamp!
        user.setLastPasswordChangeAt(LocalDateTime.now());

        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUsername(String techUsername, UsernameUpdateRequest usernameUpdateRequest) {
        User targetUser = userRepository.findById(usernameUpdateRequest.targetUserId())
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        User actorUser = userRepository.findByUsername(techUsername).orElseThrow(() -> new RuntimeException("Tech user not found"));

        String structuralChange = String.format("Changed username from '%s' to '%s'.", targetUser.getUsername(), usernameUpdateRequest.newUsername());

        // Change the username
        targetUser.setUsername(usernameUpdateRequest.newUsername());
        userRepository.save(targetUser);

        // If they provided a reason, tack it onto the end of the description
        String finalDescription = usernameUpdateRequest.reason() != null && !usernameUpdateRequest.reason().isBlank()
                ? structuralChange + " Reason provided: " + usernameUpdateRequest.reason()
                : structuralChange;

        // Logged
        auditServiceImpl.logAction(AuditAction.USERNAME_UPDATE, actorUser, targetUser, finalDescription);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(AllUsersRequest allUsersRequest, Pageable pageable) {
        LocalDateTime cutoffDate = null;

        // If a month was selected, find its absolute starting point
        if (allUsersRequest.cutoffMonth() != null) {
            // e.g., If request.month() is May 2025 -> 2025-05-01T00:00:00
            cutoffDate = allUsersRequest.cutoffMonth().atDay(1).atStartOfDay();
        }

        // Execute the optimized database query
        Page<User> users = userRepository.findWithFilters(
                allUsersRequest.role(),
                cutoffDate,
                pageable
        );
        return users.map(user -> new UserDto(user.getFirstName(), user.getLastName(), user.getProfilePicture(), user.getEmail(), user.getRole(), YearMonth.from(user.getCreatedAt())));
    }
}
