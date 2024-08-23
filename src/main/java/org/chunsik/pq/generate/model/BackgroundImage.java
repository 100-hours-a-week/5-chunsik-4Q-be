package org.chunsik.pq.generate.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Background_image")
public class BackgroundImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Lob
    @Column(name = "url", columnDefinition = "LONGTEXT")
    private String url;

    @Column(name = "size")
    private Long size;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Builder
    public BackgroundImage(Integer userId, String url, Long size, Integer categoryId) {
        this.userId = userId;
        this.url = url;
        this.size = size;
        this.categoryId = categoryId;
        this.createdAt = LocalDateTime.now();  // 객체 생성 시 자동으로 현재 시간 설정
    }
}
