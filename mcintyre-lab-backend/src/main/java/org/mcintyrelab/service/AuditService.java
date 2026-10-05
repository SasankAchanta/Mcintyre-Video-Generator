package org.mcintyrelab.service;

import org.mcintyrelab.model.User;
import org.mcintyrelab.model.enums.AuditAction;

public interface AuditService {
    public void logAction(AuditAction action, User actor, User target, String description);
}
