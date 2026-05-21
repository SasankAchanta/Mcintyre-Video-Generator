package org.mcintyrelab.repository;

import org.mcintyrelab.model.User;
import org.mcintyrelab.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:cutoffDate IS NULL OR u.createdAt >= :cutoffDate)")
    Page<User> findWithFilters(
            @Param("role") Role role,
            @Param("cutoffDate") LocalDateTime cutoffDate, // Expects a single date now!
            Pageable pageable
    );
}
