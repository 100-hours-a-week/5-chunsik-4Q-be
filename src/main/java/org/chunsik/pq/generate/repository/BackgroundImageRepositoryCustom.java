package org.chunsik.pq.generate.repository;

import org.chunsik.pq.gallery.dto.BackgroundImageDTO;
import org.chunsik.pq.gallery.model.GallerySort;
import org.chunsik.pq.generate.dto.RelateImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BackgroundImageRepositoryCustom {
    Page<BackgroundImageDTO> findByTagAndCategory(String tagName, String categoryName, GallerySort sort, Pageable pageable);

    List<RelateImageDTO> findRelateImgByTags(@Param("tagIds") List<Long> tagIds, Long backgroundImgId);
}
