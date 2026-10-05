package org.mcintyrelab.model;

import jakarta.persistence.*;
import lombok.*;
import org.mcintyrelab.model.enums.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs") // Best practice: pluralized table names
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "audit_log_id", updatable = false, nullable = false)
    private UUID auditLogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, updatable = false)
    private AuditAction action;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading protects application performance
    @JoinColumn(name = "performed_by_user_id", nullable = false, updatable = false)
    private User performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false, updatable = false) // Fixed: Explicit naming
    private User targetUser;

    @Column(name = "description", nullable = false, updatable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false) // Clarified naming
    private LocalDateTime timestamp;

    @PrePersist // Fixed: Automatically sets timestamp upon creation (row), NOT update!
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}