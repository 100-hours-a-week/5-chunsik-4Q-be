package org.chunsik.pq.gallery.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.model.UserLike;
import org.chunsik.pq.gallery.repository.UserLikeRepository;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.chunsik.pq.generate.repository.BackgroundImageRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class GalleryService {

    private final UserLikeRepository likeRepository;
    private final UserManager userManager;
    private final BackgroundImageRepository backgroundImageRepository;

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

    @Transactional
    public void addViewCount(Long imageId) {
        BackgroundImage backgroundImage = backgroundImageRepository.findById(imageId).orElseThrow(() -> new NoSuchElementException("backgroundImage not found By Id:" + imageId));
        backgroundImage.addViewCount();
        backgroundImageRepository.save(backgroundImage);
    }
}