package org.mcintyrelab.repository;

import org.mcintyrelab.model.AuditLog;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AuditLogRepository extends CrudRepository<AuditLog, UUID> {
}
