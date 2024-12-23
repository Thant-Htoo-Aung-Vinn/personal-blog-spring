package com.vinn.personalBlog.api.post.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private String postTitle;
    private String postDescription;
    private String categoryCode;
}
