package org.mcintyrelab.model;

import jakarta.persistence.*;
import lombok.*;
import org.mcintyrelab.model.enums.VideoType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "csv_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Csv {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "csv_id", nullable = false, updatable = false)
    private UUID csvId;

    @OneToOne(fetch = FetchType.LAZY) //Trick database since not every video will have a csv
    @JoinColumn(name = "video_id", nullable = false, unique = true, updatable = false)
    private Video video;

    @Column(name = "csv_url", nullable = false, updatable = false)
    private String csvUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
