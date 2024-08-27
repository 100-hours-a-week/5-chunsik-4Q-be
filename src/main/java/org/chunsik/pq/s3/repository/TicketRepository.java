package org.chunsik.pq.s3.repository;

import org.chunsik.pq.s3.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t.id FROM Ticket t WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    List<Long> findTicketIdsByUserIdOrderByCreatedAtDesc(Long userId);

    List<Ticket> findByIdIn(List<Long> ticketIds);
}
