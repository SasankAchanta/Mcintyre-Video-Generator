package org.mcintyrelab.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "untrained")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Untrained {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "untrained_video_id")
    private Integer untrainedVideoId;

    @ManyToOne(fetch = FetchType.LAZY) // Best practice to use LAZY for performance
    @JoinColumn(name = "user_id", nullable = false) // This links to the 'id' column in the 'users' table
    private User user;

    @Column(name = "video_url", unique = true)
    private String videoUrl;

    @Column(name = "is_trained")
    private Boolean isTrained;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
