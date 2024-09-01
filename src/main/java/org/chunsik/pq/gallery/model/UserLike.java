package org.chunsik.pq.gallery.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table(name = "user_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "photoBackgroundId"})
})
@Entity
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "photoBackgroundId", nullable = false)
    private Long photoBackgroundId;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    public UserLike(Long userId, Long photoBackgroundId, LocalDateTime createdAt) {
        this.userId = userId;
        this.photoBackgroundId = photoBackgroundId;
        this.createdAt = createdAt;
    }
}
