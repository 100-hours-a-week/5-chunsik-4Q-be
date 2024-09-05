package org.chunsik.pq.generate.repository;

import org.chunsik.pq.generate.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    Optional<Tag> findAllByEngName(String engName);

    @Query("SELECT tg.name, tbi.photoBackgroundId FROM Tag tg " +
           "JOIN TagBackgroundImage tbi ON tbi.tagId = tg.id")
    List<Object[]> findAllTagsWithBackgroundIds();
}