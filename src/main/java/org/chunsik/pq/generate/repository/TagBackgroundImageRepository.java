package org.chunsik.pq.generate.repository;

import org.chunsik.pq.generate.model.TagBackgroundImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TagBackgroundImageRepository extends JpaRepository<TagBackgroundImage, Long> {
    @Query("SELECT t.tagId FROM TagBackgroundImage t " +
            "WHERE t.createdAt >= :week " +
            "GROUP BY t.tagId " +
            "ORDER BY COUNT(t.tagId) DESC")
    List<Long> findTopTagId(LocalDateTime week);
}