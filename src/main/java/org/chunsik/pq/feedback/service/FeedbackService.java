package org.chunsik.pq.feedback.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.feedback.dto.FeedbackDTO;
import org.chunsik.pq.feedback.model.Feedback;
import org.chunsik.pq.feedback.model.Feedback.Gender;
import org.chunsik.pq.feedback.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(FeedbackDTO feedbackDTO) {
        // 생성자를 사용하여 Feedback 객체를 생성
        Feedback feedback = new Feedback(
                feedbackDTO.getUserId(),
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

        return feedbackRepository.save(feedback);
    }
}
