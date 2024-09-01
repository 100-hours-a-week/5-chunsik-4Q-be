package org.chunsik.pq.gallery.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.model.UserLike;
import org.chunsik.pq.gallery.repository.UserLikeRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GalleryService {

    private final UserLikeRepository likeRepository;
    private final UserManager userManager;

    @Transactional
    public void addLike(Long imageId) {
        userManager.currentUser().ifPresent(user -> {
            UserLike like = new UserLike(
                    user.getId(),
                    imageId,
                    LocalDateTime.now()
            );
            likeRepository.save(like);
        });
    }

    @Transactional
    public void removeLike(Long imageId) {
        Long userId = userManager.currentUser().map(CustomUserDetails::getId).orElseThrow();

        likeRepository.deleteByUserIdAndPhotoBackgroundId(userId, imageId);
    }

}
