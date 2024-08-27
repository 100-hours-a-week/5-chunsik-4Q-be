package org.chunsik.pq.s3.repository;

import org.chunsik.pq.s3.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t JOIN FETCH t.backgroundImage b WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsByUserIdWithBackgroundImageOrderByCreatedAtDesc(@Param("userId") Long userId);

}
