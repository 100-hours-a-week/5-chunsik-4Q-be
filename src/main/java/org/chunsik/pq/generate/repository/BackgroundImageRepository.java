package org.chunsik.pq.generate.repository;

import jakarta.persistence.Tuple;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BackgroundImageRepository extends JpaRepository<BackgroundImage, Long>, BackgroundImageRepositoryCustom {
    @Query(value = """
            SELECT bi.id, bi.url
            FROM background_image as bi
                JOIN tag_background_image as tbi
                    ON bi.id = tbi.photo_background_id AND tbi.tag_id IN (:tagIds)
                JOIN tag_background_image as whole
                    ON whole.photo_background_id = bi.id
            WHERE bi.id != :backgroundImgId
            GROUP BY bi.id
            ORDER BY COUNT(DISTINCT tbi.tag_id) DESC, COUNT(DISTINCT whole.tag_id) ASC
            """, nativeQuery = true)
    Slice<Tuple> findRelateImgByTags(@Param("tagIds") List<Long> tagIds, Pageable pageable, Long backgroundImgId);
}
