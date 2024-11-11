package org.chunsik.pq.generate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "tag_background_image")
public class TagBackgroundImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "photo_background_id")
    private Long photoBackgroundId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TagBackgroundImage(Long tagId, Long photoBackgroundId, LocalDateTime createdAt) {
        this.tagId = tagId;
        this.photoBackgroundId = photoBackgroundId;
        this.createdAt = createdAt;
    }
}
