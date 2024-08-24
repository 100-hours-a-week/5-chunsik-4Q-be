package org.chunsik.pq.feedback.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.feedback.dto.FeedbackDTO;
import org.chunsik.pq.feedback.model.Feedback;
import org.chunsik.pq.feedback.model.Feedback.Gender;
import org.chunsik.pq.feedback.repository.FeedbackRepository;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.model.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public void saveFeedback(FeedbackDTO feedbackDTO) {
        Integer userId = null;

        // 로그인 사용자와 비로그인 사용자 식별
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails details = (UserDetails) authentication.getPrincipal();
            String email = details.getUsername();

            // 이메일을 통해 userId 찾기
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                userId = userOptional.get().getId();
            }
        }

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
