package org.mcintyrelab.service.impl;

import org.mcintyrelab.model.AuditLog;
import org.mcintyrelab.model.User;
import org.mcintyrelab.model.enums.AuditAction;
import org.mcintyrelab.repository.AuditLogRepository;
import org.mcintyrelab.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY) // Ensures it runs inside the same DB transaction
    @Override
    public void logAction(AuditAction action, User actor, User target, String description) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(actor)
                .targetUser(target)
                .description(description)
                // timestamp is handled automatically by @PrePersist!
                .build();

        auditLogRepository.save(auditLog);
    }
}
