package org.chunsik.pq.generate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "tag_background_image")
public class TagBackgroundImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "photo_background_id")
    private Long photoBackgroundId;

    public TagBackgroundImage(Integer tagId, Long photoBackgroundId) {
        this.tagId = tagId;
        this.photoBackgroundId = photoBackgroundId;
    }
}
