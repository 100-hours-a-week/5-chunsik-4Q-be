package org.chunsik.pq.s3.repository;

import org.chunsik.pq.s3.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
