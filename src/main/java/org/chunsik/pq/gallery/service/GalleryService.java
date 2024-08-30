package org.chunsik.pq.gallery.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.exception.UserLikeNotFoundException;
import org.chunsik.pq.gallery.model.UserLike;
import org.chunsik.pq.gallery.repository.UserLikeRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GalleryService {

    private final UserLikeRepository likeRepository;
    private final UserManager userManager;

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

    public void removeLike(Long imageId) {
        // 현재 로그인한 사용자의 ID를 가져옴
        Long userId = userManager.currentUser().map(CustomUserDetails::getId).orElseThrow();

        // 해당 사용자와 이미지의 좋아요 여부 확인
        Optional<UserLike> like = likeRepository.findByUserIdAndPhotoBackgroundId(userId, imageId);

        if (like.isPresent()) {
            // 좋아요가 존재하면 삭제
            likeRepository.delete(like.get());
        } else {
            // 존재하지 않는 경우 예외 발생
            throw new UserLikeNotFoundException("Like does not exist for the given image ID: " + imageId);
        }
    }

}
