package org.chunsik.pq.s3.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Photo_background")
public class PhotoBackground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Lob
    @Column(name = "url", nullable = false, columnDefinition = "LONGTEXT")
    private String url;

    @Column(name = "size")
    private Long size;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(name = "first_tag")
    private String firstTag;

    @Column(name = "second_tag")
    private String secondTag;

    @Column(name = "third_tag")
    private String thirdTag;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Builder
    public PhotoBackground(Long userId, String url, Long size, String categoryId, String firstTag, String secondTag, String thirdTag) {
        this.userId = userId;
        this.url = url;
        this.size = size;
        this.categoryId = categoryId;
        this.firstTag = firstTag;
        this.secondTag = secondTag;
        this.thirdTag = thirdTag;
        this.createdAt = LocalDateTime.now();  // 객체 생성 시 자동으로 현재 시간 설정
    }
}