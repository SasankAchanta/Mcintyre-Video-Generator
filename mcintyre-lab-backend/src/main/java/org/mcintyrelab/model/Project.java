package org.mcintyrelab.model;

import jakarta.persistence.*;
import lombok.*;
import org.mcintyrelab.model.enums.ModelType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "model_type", nullable = false, updatable = false)
    private ModelType modelType;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description", nullable = false)
    private String description;

    // THE CORRECTION: Use mappedBy instead of @JoinColumn
    // Added cascade and orphanRemoval so deleting a project automatically wipes its associated videos!
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default // Keeps Lombok from initializing this list as null
    private List<Video> videos = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}