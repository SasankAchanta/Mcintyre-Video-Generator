package org.mcintyrelab.repository;

import org.mcintyrelab.model.Project;
import org.mcintyrelab.model.enums.ModelType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE " +
            "(:modelType IS NULL OR p.modelType = :modelType) AND " +
            "(:cutoffDate IS NULL OR p.createdAt >= :cutoffDate)")
    Page<Project> findWithFilters(
            @Param("modelType") ModelType modelType,
            @Param("cutoffDate") LocalDateTime cutoffDate,
            Pageable pageable
    );

    @Query("SELECT p FROM Project p WHERE " +
            "p.user.username = :username AND " + // <-- THE CHANGE: Crucial security filter!
            "(:modelType IS NULL OR p.modelType = :modelType) AND " +
            "(:cutoffDate IS NULL OR p.createdAt >= :cutoffDate)")
    Page<Project> findWithFiltersAndUser(
            @Param("username") String username,        // <-- Added this parameter
            @Param("modelType") ModelType modelType,
            @Param("cutoffDate") LocalDateTime cutoffDate,
            Pageable pageable
    );

}
