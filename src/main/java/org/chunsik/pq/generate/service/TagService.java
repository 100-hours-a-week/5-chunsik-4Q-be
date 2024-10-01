package org.chunsik.pq.generate.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.exception.UnauthorizedException;
import org.chunsik.pq.generate.repository.TagRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserManager userManager;

    public List<String> getLastUsedTagsForCurrentUser() {
        // 현재 사용자 ID 가져오기
        Long currentUserId = userManager.currentUser().map(CustomUserDetails::getId).orElse(null);

        // 찾을 수 없다면 예외 던지기
        if (currentUserId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        // 사용자 ID를 사용하여 태그 목록 조회
        return tagRepository.findTagNamesByLastUsedBackgroundImage(currentUserId);
    }
}