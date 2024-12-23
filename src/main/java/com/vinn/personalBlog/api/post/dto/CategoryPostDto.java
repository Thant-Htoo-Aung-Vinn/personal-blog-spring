package com.vinn.personalBlog.api.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CategoryPostDto {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String iconName;
    private List<PostDto> posts;
}
