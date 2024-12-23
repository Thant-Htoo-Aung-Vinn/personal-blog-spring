package com.vinn.personalBlog.api.category.service;

import com.vinn.personalBlog.api.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryByCode(String code);
    CategoryDto saveOrUpdateCategory(CategoryDto categoryDto);
    void deleteCategory(String code);
}
