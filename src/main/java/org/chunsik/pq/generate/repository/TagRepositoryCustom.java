package org.chunsik.pq.generate.repository;

import java.util.List;

public interface TagRepositoryCustom {
    List<String> findTagNamesByLastUsedBackgroundImage(Long userId);
}

