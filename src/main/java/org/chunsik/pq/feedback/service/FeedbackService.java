package org.chunsik.pq.feedback.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.feedback.dto.FeedbackDTO;
import org.chunsik.pq.feedback.model.Feedback;
import org.chunsik.pq.feedback.model.Feedback.Gender;
import org.chunsik.pq.feedback.repository.FeedbackRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import io.sentry.Sentry;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserManager userManager;

    public void saveFeedback(FeedbackDTO feedbackDTO) {

        // 로그인 사용자와 비로그인 사용자 식별
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        Long userId = currentUser.map(CustomUserDetails::getId).orElse(null);

        Feedback feedback = new Feedback(
                userId,
                feedbackDTO.getStarRate(),
                feedbackDTO.getComment(),
                new Timestamp(System.currentTimeMillis()),
                feedbackDTO.getEase(),
                feedbackDTO.getDesign(),
                feedbackDTO.getPerformance(),
                feedbackDTO.getFeature(),
                feedbackDTO.getRecommendation(),
                feedbackDTO.getReuse(),
                feedbackDTO.getAgeGroup(),
                Gender.valueOf(feedbackDTO.getGender().toUpperCase())
        );

        feedbackRepository.save(feedback);

        // Sentry Scope 설정
        Sentry.configureScope(scope -> {
            Map<String, Object> feedbackContext = new HashMap<>();
            feedbackContext.put("유저ID", userId != null ? userId.toString() : "Anonymous");
            feedbackContext.put("별점", feedbackDTO.getStarRate());
            feedbackContext.put("추가의견", feedbackDTO.getComment());
            feedbackContext.put("쉬움", feedbackDTO.getEase());
            feedbackContext.put("디자인", feedbackDTO.getDesign());
            feedbackContext.put("성능", feedbackDTO.getPerformance());
            feedbackContext.put("기능작동", feedbackDTO.getFeature());
            feedbackContext.put("추천의향", feedbackDTO.getRecommendation());
            feedbackContext.put("재사용의향", feedbackDTO.getReuse());
            feedbackContext.put("나이대", feedbackDTO.getAgeGroup());
            feedbackContext.put("성별", feedbackDTO.getGender());

            scope.setContexts("feedback", feedbackContext);
        });

        Sentry.captureMessage("Feedback submitted by user: " + (userId != null ? userId : "Anonymous"));

    }
}
