package com.vinn.personalBlog.api.post.repository;

import com.vinn.personalBlog.api.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for handling CRUD operations for posts.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    void deleteById(Long id);
}
