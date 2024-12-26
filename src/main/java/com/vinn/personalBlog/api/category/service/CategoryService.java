package com.vinn.personalBlog.api.category.service;

import com.vinn.personalBlog.api.category.dto.CategoryDto;
import com.vinn.personalBlog.api.post.dto.CategoryPostDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryByCode(String code);
    CategoryDto saveOrUpdateCategory(CategoryDto categoryDto);
    void deleteCategory(String code);
    List<CategoryPostDto> getAllCategoriesWithPosts();
    CategoryPostDto getPostsByCategoryName(String categoryName);
}
