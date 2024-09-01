package org.chunsik.pq.generate.repository;

import org.chunsik.pq.gallery.dto.BackgroundImageDTO;
import org.chunsik.pq.gallery.model.GallerySort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BackgroundImageRepositoryCustom {
    Page<BackgroundImageDTO> findByTagAndCategory(String tagName, String categoryName, GallerySort sort, Pageable pageable);
}
