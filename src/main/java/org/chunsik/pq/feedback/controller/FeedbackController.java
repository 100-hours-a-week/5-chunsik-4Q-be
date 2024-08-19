package org.chunsik.pq.feedback.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.feedback.dto.FeedbackDTO;
import org.chunsik.pq.feedback.model.Feedback;
import org.chunsik.pq.feedback.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            Feedback feedback = feedbackService.saveFeedback(feedbackDTO);
            return new ResponseEntity<>(feedback, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // 400 Bad Request - 잘못된 입력 데이터가 있는 경우
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            // 500 Internal Server Error - 서버에서 처리 중 오류가 발생한 경우
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the request.", e);
        }
    }
}
