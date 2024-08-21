package org.chunsik.pq.feedback.repository;

import org.chunsik.pq.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
