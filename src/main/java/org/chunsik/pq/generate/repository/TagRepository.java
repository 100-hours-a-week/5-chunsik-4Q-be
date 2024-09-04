package org.chunsik.pq.generate.repository;

import org.chunsik.pq.generate.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);


    Optional<Tag> findAllByEngName(String engName);
}