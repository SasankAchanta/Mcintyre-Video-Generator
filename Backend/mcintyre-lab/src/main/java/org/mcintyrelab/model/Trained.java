package org.mcintyrelab.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trained")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trained {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trained_video_id")
    private Integer trainedVideoId;

    @ManyToOne(fetch = FetchType.LAZY) // Best practice to use LAZY for performance
    @JoinColumn(name = "user_id", nullable = false) // This links to the 'id' column in the 'users' table
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "untrained_id", nullable = false)
    private Untrained untrained;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
