package com.vinn.personalBlog.api.category.repository;

import com.vinn.personalBlog.api.category.model.Category;
import com.vinn.personalBlog.api.post.dto.CategoryPostDto;
import com.vinn.personalBlog.api.post.dto.PostDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for handling CRUD operations for categories.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCode(String code);

    boolean existsByCode(String code);

    void deleteByCode(String code);

    @Query(value = """
        SELECT c.id AS categoryId, c.name AS categoryName, c.description AS categoryDescription, 
               c.icon_name AS iconName, p.id AS postId, p.title AS postTitle, 
               p.description AS postDescription, c.code AS categoryCode
        FROM categories c
        LEFT JOIN posts p ON c.id = p.category_id
        ORDER BY c.name, p.created_at DESC
        LIMIT 3
    """, nativeQuery = true)
    List<Object[]> fetchCategoriesWithPostsRaw();

    @Query("""
        SELECT new com.vinn.personalBlog.api.post.dto.PostDto(
            p.id, p.title, p.description, p.category.code
        )
        FROM Post p
        WHERE p.category.name = :categoryName
        ORDER BY p.createdAt DESC
    """)
    List<PostDto> fetchPostsByCategoryName(@Param("categoryName") String categoryName);

    Category findByName(String categoryName);
}