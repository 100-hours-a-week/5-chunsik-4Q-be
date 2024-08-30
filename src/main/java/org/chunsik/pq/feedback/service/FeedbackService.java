package org.chunsik.pq.feedback.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.feedback.dto.FeedbackDTO;
import org.chunsik.pq.feedback.model.Feedback;
import org.chunsik.pq.feedback.model.Feedback.Gender;
import org.chunsik.pq.feedback.repository.FeedbackRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final UserManager userManager;

    public void saveFeedback(FeedbackDTO feedbackDTO) {
        Long userId = null;

        // 로그인 사용자와 비로그인 사용자 식별
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        userId = currentUser.map(CustomUserDetails::getId).orElse(null);

        // Feedback 객체 생성
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
    }
}
