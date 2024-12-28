package com.vinn.personalBlog.api.post.repository;

import com.vinn.personalBlog.api.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for handling CRUD operations for posts.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    void deleteById(Long id);
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(org.springframework.data.domain.Pageable limit);
}
