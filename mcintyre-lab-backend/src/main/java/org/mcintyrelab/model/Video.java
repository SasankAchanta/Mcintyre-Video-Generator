package org.mcintyrelab.model;

import jakarta.persistence.*;
import lombok.*;
import org.mcintyrelab.model.enums.VideoType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "video")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "video_id", nullable = false, updatable = false)
    private UUID videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    @Column(name = "video_url", nullable = false, updatable = false)
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "video_type", nullable = false, updatable = false)
    private VideoType videoType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
