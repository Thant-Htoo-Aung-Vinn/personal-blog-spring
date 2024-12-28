package com.vinn.personalBlog.api.post.service;

import com.vinn.personalBlog.api.post.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getAllPosts();
    PostDto getPostById(Long id);
    PostDto saveOrUpdatePost(PostDto postDto);
    void deletePost(Long id);
    List<PostDto> getRecentPosts(int limit);
}
