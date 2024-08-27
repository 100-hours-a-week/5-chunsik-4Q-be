package org.chunsik.pq.generate.repository;

import org.chunsik.pq.generate.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    @Query("SELECT c.name FROM Category c WHERE c.id = :categoryId")
    String findNameById(@Param("categoryId") Long categoryId);
}
