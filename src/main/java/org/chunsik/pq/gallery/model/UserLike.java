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
    private Long id;

    private Long userId;
    private Long photoBackgroundId;
    private LocalDateTime createdAt;

    public UserLike(Long userId, Long photoBackgroundId, LocalDateTime createdAt) {
        this.userId = userId;
        this.photoBackgroundId = photoBackgroundId;
        this.createdAt = createdAt;
    }
}
