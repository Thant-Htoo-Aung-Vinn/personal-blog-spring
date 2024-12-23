package com.vinn.personalBlog.api.post.service.impl;

import com.vinn.personalBlog.api.category.model.Category;
import com.vinn.personalBlog.api.category.repository.CategoryRepository;
import com.vinn.personalBlog.api.exception.EntityNotFoundException;
import com.vinn.personalBlog.api.post.dto.PostDto;
import com.vinn.personalBlog.api.post.model.Post;
import com.vinn.personalBlog.api.post.repository.PostRepository;
import com.vinn.personalBlog.api.post.service.PostService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for handling business logic related to posts.
 */
@Service
public class PostServiceImpl implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PostDto> getAllPosts() {
        logger.info("Fetching all posts");
        return postRepository.findAll().stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long id) {
        logger.info("Fetching post by id: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto saveOrUpdatePost(PostDto postDto) {
        Post post;

        if (postDto.getPostId() != null) {
            post = postRepository.findById(postDto.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Post with id " + postDto.getPostId() + " not found"));

            post.setTitle(postDto.getPostTitle());
            post.setDescription(postDto.getPostDescription());
        } else {
            post = new Post();
            post.setTitle(postDto.getPostTitle());
            post.setDescription(postDto.getPostDescription());
            post.setAuthor("Thant Htoo Aung");
        }

        Category category = categoryRepository.findByCode(postDto.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("Category not found for code: " + postDto.getCategoryCode()));
        post.setCategory(category);

        Post savedPost = postRepository.save(post);

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    public void deletePost(Long id) {
        logger.info("Deleting post with id: {}", id);
        postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
        postRepository.deleteById(id);
    }
}
