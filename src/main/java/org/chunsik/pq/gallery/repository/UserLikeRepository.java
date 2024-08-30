package org.chunsik.pq.gallery.repository;

import org.chunsik.pq.gallery.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    Optional<UserLike> findByUserIdAndPhotoBackgroundId(Long userId, Long photoBackgroundId);
}
