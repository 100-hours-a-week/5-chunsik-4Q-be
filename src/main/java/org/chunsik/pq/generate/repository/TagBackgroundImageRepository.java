package org.chunsik.pq.generate.repository;

import org.chunsik.pq.generate.model.TagBackgroundImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagBackgroundImageRepository extends JpaRepository<TagBackgroundImage, Long> {
    @Query("SELECT tbi.tagId FROM TagBackgroundImage tbi WHERE tbi.photoBackgroundId = :photoBackgroundId")
    List<Long> findTagIdsByPhotoBackgroundId(Long photoBackgroundId);
}