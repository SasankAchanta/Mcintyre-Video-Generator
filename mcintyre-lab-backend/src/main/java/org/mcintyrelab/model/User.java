package org.mcintyrelab.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.mcintyrelab.model.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "email", nullable = false, unique = true, length = 22)
    private String email;

    @Column(name = "username", nullable = false, unique = true, length = 9)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Password won't appear in api responses
    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "last_password_change_at")
    private LocalDateTime lastPasswordChangeAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "verification_token")
    private Integer verificationToken;

    @Column(name = "token_expiry_date")
    private LocalDateTime tokenExpiryDate;

    @PrePersist
    public void prePersist() {
        profilePicture = UUID.randomUUID().toString(); // Change this to be default png later
        lastPasswordChangeAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isVerified = false; // Users start as disabled until they verify
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
