package org.chunsik.pq.image.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "photo_background")
public class PhotoBackground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "size")
    private Integer size;

    @Column(name = "category_id")
    private Long categoryId;
}
