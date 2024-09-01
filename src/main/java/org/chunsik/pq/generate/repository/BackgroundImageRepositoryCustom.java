package org.chunsik.pq.generate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BackgroundImageRepositoryCustom {
    Page<Map<String, Object>> findByTagAndCategory(String tagName, String categoryName, String sort, Pageable pageable);
}
