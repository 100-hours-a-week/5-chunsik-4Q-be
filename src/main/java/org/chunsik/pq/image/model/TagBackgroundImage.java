package org.chunsik.pq.image.model;

import jakarta.persistence.*;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tag_background_image")
public class TagPhotoBackground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_id")
    private Long tagId;

    @ManyToOne
    @JoinColumn(name = "photo_background_id", insertable = false, updatable = false)
    private PhotoBackground photoBackground;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}