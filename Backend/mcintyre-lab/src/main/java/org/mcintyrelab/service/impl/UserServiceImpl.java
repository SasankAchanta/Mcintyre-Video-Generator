package org.mcintyrelab.service.impl;

import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.user.request.*;
import org.mcintyrelab.exception.badrequest.UserNotFoundException;
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
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        // verify current password is correct
        if (!passwordEncoder.matches(passwordUpdateRequest.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // ensure current and new password are not the same
        if (passwordEncoder.matches(passwordUpdateRequest.newPassword(), user.getPassword())) {
            throw new RuntimeException("Current password and new password cannot be the same");
        }

        // Update your compliance tracking timestamp!
        user.setLastPasswordChangeAt(LocalDateTime.now());

        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUsername(String actorUsername, UsernameUpdateRequest usernameUpdateRequest) {
        User targetUser = userRepository.findById(usernameUpdateRequest.targetUserId())
                .orElseThrow(() -> new UserNotFoundException(usernameUpdateRequest.targetUserId().toString()));

        User actorUser = userRepository.findByUsername(actorUsername).orElseThrow(() -> new UserNotFoundException(actorUsername));

        String structuralChange = String.format("Changed username from '%s' to '%s'.", targetUser.getUsername(), usernameUpdateRequest.newUsername());

        // Change the username
        targetUser.setUsername(usernameUpdateRequest.newUsername());
        userRepository.save(targetUser);

        // If they provided a reason, tack it onto the end of the description
        String finalDescription = usernameUpdateRequest.reason() != null && !usernameUpdateRequest.reason().isBlank()
                ? structuralChange + " Reason provided: " + usernameUpdateRequest.reason()
                : structuralChange;

        // Log it
        auditServiceImpl.logAction(AuditAction.USERNAME_UPDATE, actorUser, targetUser, finalDescription);
    }

    @Override
    public void updateRole(String actorUsername, RoleUpdateRequest roleUpdateRequest) {
        User targetUser = userRepository.findById(roleUpdateRequest.targetUserId()).orElseThrow(() -> new UserNotFoundException(roleUpdateRequest.targetUserId().toString()));
        User actorUser = userRepository.findByUsername(actorUsername).orElseThrow(() -> new UserNotFoundException(actorUsername));

        String structuralChange = String.format("Changed role from '%s' to '%s'.", targetUser.getRole(), roleUpdateRequest.newRole());

        // Change the role
        targetUser.setRole(roleUpdateRequest.newRole());
        userRepository.save(targetUser);

        // If they provided a reason, tack it onto the end of the description
        String finalDescription = roleUpdateRequest.reason() != null && !roleUpdateRequest.reason().isBlank()
                ? structuralChange + " Reason provided: " + roleUpdateRequest.reason()
                : structuralChange;

        // Log it
        auditServiceImpl.logAction(AuditAction.ROLE_UPDATE, actorUser, targetUser, finalDescription);
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

    @Override
    @Transactional
    public void updateName(String actorUsername, NameUpdateRequest nameUpdateRequest) {
        User targetUser = userRepository.findById(nameUpdateRequest.targetUserId())
                .orElseThrow(() -> new UserNotFoundException(nameUpdateRequest.targetUserId().toString()));
        User actorUser = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new RuntimeException(actorUsername));

        // 1. Capture the original name BEFORE modifying the object
        String originalFullName = targetUser.getFirstName() + " " + targetUser.getLastName();

        // 2. Clean up input and split safely by spaces
        String cleanedName = nameUpdateRequest.newName().trim();
        String[] nameParts = cleanedName.split("\\s+", 2);

        String newFirstname = nameParts[0];
        String newLastname = (nameParts.length > 1) ? nameParts[1] : "";

        boolean firstNameChanged = !newFirstname.equals(targetUser.getFirstName());
        boolean lastNameChanged = !newLastname.equals(targetUser.getLastName());

        // 3. Only execute database writes and logs if an actual change occurred
        if (firstNameChanged || lastNameChanged) {

            if (!newFirstname.isEmpty() && firstNameChanged) {
                targetUser.setFirstName(newFirstname);
            }
            if (!newLastname.isEmpty() && lastNameChanged) {
                targetUser.setLastName(newLastname);
            }

            // Save the updates to the database (only once!)
            userRepository.save(targetUser);

            // 4. Build description strings inside this scope where they belong
            String structuralChange = String.format("Changed name from '%s' to '%s'.", originalFullName, cleanedName);

            // 5. Append reason if provided
            String finalDescription = nameUpdateRequest.reason() != null && !nameUpdateRequest.reason().isBlank()
                    ? structuralChange + " Reason provided: " + nameUpdateRequest.reason()
                    : structuralChange;

            // 6. Log it
            auditServiceImpl.logAction(AuditAction.NAME_UPDATE, actorUser, targetUser, finalDescription);
        }
        // If nothing changed, the method reaches the end here and exits cleanly!
    }
}
