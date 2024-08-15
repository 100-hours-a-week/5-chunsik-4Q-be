package org.chunsik.pq.s3.repository;

import org.chunsik.pq.s3.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3Repository extends JpaRepository<Image, Long> {
}
