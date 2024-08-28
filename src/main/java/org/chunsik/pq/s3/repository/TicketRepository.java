package org.chunsik.pq.s3.repository;

import org.chunsik.pq.s3.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // userId로 티켓을 필터링하고 생성일자 내림차순으로 정렬하는 메서드
    @Query("SELECT t FROM Ticket t JOIN FETCH t.backgroundImage b WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsByUserIdWithBackgroundImageOrderByCreatedAtDesc(@Param("userId") Long userId);

    // userId로 티켓을 필터링하고 제목을 포함하는 티켓을 생성일자 내림차순으로 정렬하는 메서드
    @Query("SELECT t FROM Ticket t WHERE t.userId = :userId AND t.title LIKE %:title% ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsByUserIdAndTitleContainingOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("title") String title);

    // 태그로 필터링된 티켓을 조회하는 메서드
    @Query("SELECT t FROM Ticket t " +
            "JOIN TagBackgroundImage tbi ON t.backgroundImage.id = tbi.photoBackgroundId " +
            "JOIN Tag tag ON tbi.tagId = tag.id " +
            "WHERE t.userId = :userId AND tag.name = :tag " +
            "ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsByUserIdAndTagNameOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("tag") String tag);


}

