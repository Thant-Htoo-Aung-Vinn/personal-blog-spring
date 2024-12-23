package com.vinn.personalBlog.api.category.controller;

import com.vinn.personalBlog.api.category.dto.CategoryDto;
import com.vinn.personalBlog.api.category.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller to manage categories within the blogging platform.
 * This controller provides endpoints for creating, retrieving, updating, and deleting categories,
 * leveraging the CategoryService to perform business logic operations.
 */
@RestController
@RequestMapping("${api.base.path}/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all categories.
     * @return a ResponseEntity containing a list of CategoryDto.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        logger.info("Received request to list all categories");
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a single category by its unique code.
     * @param code the unique code of the category.
     * @return a ResponseEntity containing the CategoryDto if found.
     */
    @GetMapping("/{code}")
    public ResponseEntity<CategoryDto> getCategoryByCode(@PathVariable String code) {
        logger.info("Received request to fetch category by code: {}", code);
        return ResponseEntity.ok(categoryService.getCategoryByCode(code));
    }

    /**
     * Creates or updates a category.
     * If the category does not exist, it is created. If it exists, it is updated.
     * @param categoryDto the category data transfer object.
     * @return a ResponseEntity with the saved CategoryDto.
     */
    @PostMapping
    public ResponseEntity<CategoryDto> saveOrUpdateCategory(@RequestBody CategoryDto categoryDto) {
        logger.info("Received request to save category: {}", categoryDto);
        CategoryDto savedCategory = categoryService.saveOrUpdateCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    /**
     * Deletes a category by its unique code.
     * @param code the unique code of the category to delete.
     * @return a ResponseEntity with HTTP status indicating success or failure.
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String code) {
        logger.info("Received request to delete category with code: {}", code);
        categoryService.deleteCategory(code);
        return ResponseEntity.ok().build();
    }
}
