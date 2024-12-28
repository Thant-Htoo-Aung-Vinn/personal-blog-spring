package com.vinn.personalBlog.api.post.controller;

import com.vinn.personalBlog.api.post.dto.PostDto;
import com.vinn.personalBlog.api.post.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller to manage posts within the blogging platform.
 */
@RestController
@RequestMapping("${api.base.path}/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Retrieves all posts.
     * @return a ResponseEntity containing a list of PostDto.
     */
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        logger.info("Received request to fetch all posts");
        List<PostDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieves a post by its ID.
     * @param id the ID of the post.
     * @return a ResponseEntity containing the PostDto.
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        logger.info("Received request to fetch post with id: {}", id);
        PostDto postDto = postService.getPostById(id);
        return ResponseEntity.ok(postDto);
    }

    /**
     * Creates or updates a post.
     * @param postDto the PostDto containing the post details.
     * @return a ResponseEntity containing the saved PostDto.
     */
    @PostMapping
    public ResponseEntity<PostDto> saveOrUpdatePost(@RequestBody PostDto postDto) {
        logger.info("Received request to create or update post: {}", postDto);
        PostDto savedPost = postService.saveOrUpdatePost(postDto);
        return ResponseEntity.ok(savedPost);
    }

    /**
     * Deletes a post by its ID.
     * @param id the ID of the post.
     * @return a ResponseEntity indicating the deletion status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        logger.info("Received request to delete post with id: {}", id);
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the most recent posts.
     *
     * @param limit the number of posts to retrieve.
     * @return a ResponseEntity containing the list of recent PostDto objects.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<PostDto>> getRecentPosts(@RequestParam(defaultValue = "3") int limit) {
        logger.info("Received request to fetch {} most recent posts", limit);
        List<PostDto> recentPosts = postService.getRecentPosts(limit);
        return ResponseEntity.ok(recentPosts);
    }
}
