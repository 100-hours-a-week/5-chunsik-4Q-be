package org.chunsik.pq.generate.repository;

import org.chunsik.pq.generate.model.BackgroundImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackgroundImageRepository extends JpaRepository<BackgroundImage, Long>, BackgroundImageRepositoryCustom {
}
