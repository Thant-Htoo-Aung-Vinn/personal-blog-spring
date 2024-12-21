package com.vinn.personalBlog.api.category.repository;

import com.vinn.personalBlog.api.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for handling CRUD operations for categories.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCode(String code);

    boolean existsByCode(String code);

    void deleteByCode(String code);
}