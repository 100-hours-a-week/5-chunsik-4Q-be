package org.chunsik.pq.s3.repository;

import org.chunsik.pq.s3.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t, b, COUNT(DISTINCT ul.id) AS likesCount " +
            "FROM Ticket t " +
            "JOIN FETCH t.backgroundImage b " +
            "LEFT JOIN UserLike ul ON ul.photoBackgroundId = b.id " +
            "WHERE t.userId = :userId " +
            "GROUP BY t.id, b.id " +
            "ORDER BY t.createdAt DESC")
    List<Object[]> findTicketsWithLikesByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    Optional<Ticket> findByBackgroundImageIdAndUrlId(Long backgroundImageId, Long shortenUrlId);

}


