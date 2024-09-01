package org.chunsik.pq.generate.repository;

import jakarta.persistence.Tuple;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BackgroundImageRepository extends JpaRepository<BackgroundImage, Long> {
    @Query(value = """
            SELECT bi.id, bi.url
            FROM (
                SELECT bi.id, bi.url, COUNT(*) AS cnt
                FROM background_image bi
                JOIN tag_background_image tbi
                ON bi.id = tbi.photo_background_id
                GROUP BY bi.id
            ) bi
            JOIN (
                SELECT *
                FROM tag_background_image tbi
                WHERE tbi.tag_id IN (:tagIds)
            ) tbi
            ON bi.id = tbi.photo_background_id
            WHERE bi.id != :backgroundImgId
            GROUP BY bi.id
            ORDER BY COUNT(DISTINCT tbi.tag_id) DESC, bi.cnt ASC
            """, nativeQuery = true)
    Slice<Tuple> findRelateImgByTags(@Param("tagIds") List<Long> tagIds, Pageable pageable, Long backgroundImgId);
}