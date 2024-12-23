package com.vinn.personalBlog.api.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private String name;
    private String code;
    private String description;
    private String iconName;
}
