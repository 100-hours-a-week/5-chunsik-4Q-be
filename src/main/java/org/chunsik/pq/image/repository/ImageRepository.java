package org.chunsik.pq.image.repository;

import org.chunsik.pq.image.model.TagPhotoBackground;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<TagPhotoBackground, Long> {

    @Query(value = "SELECT pb.image_path, COUNT(tpb.id) as count " +
            "FROM tag_photo_background tpb " +
            "JOIN photo_background pb ON tpb.photo_background_id = pb.id " +
            "WHERE tpb.created_at >= NOW() - INTERVAL 1 DAY " +
            "GROUP BY pb.image_path " +
            "ORDER BY count DESC", nativeQuery = true)
    List<Object[]> findTopPhotoBackgrounds();
}