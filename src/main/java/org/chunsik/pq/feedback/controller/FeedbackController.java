package org.chunsik.pq.feedback.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.feedback.dto.FeedbackDTO;
import org.chunsik.pq.feedback.model.Feedback;
import org.chunsik.pq.feedback.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        Feedback feedback = feedbackService.saveFeedback(feedbackDTO);
        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }
}
