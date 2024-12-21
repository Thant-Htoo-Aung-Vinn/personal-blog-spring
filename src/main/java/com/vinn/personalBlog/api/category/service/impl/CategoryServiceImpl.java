package com.vinn.personalBlog.api.category.service.impl;

import com.vinn.personalBlog.api.category.dto.CategoryDto;
import com.vinn.personalBlog.api.category.model.Category;
import com.vinn.personalBlog.api.category.repository.CategoryRepository;
import com.vinn.personalBlog.api.category.service.CategoryService;
import java.util.Base64;

import com.vinn.personalBlog.api.exception.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service responsible for handling business logic and interaction
 * between the Category domain model and the persistence layer.
 *
 * This service adheres to Domain-Driven Design principles by encapsulating
 * operations on the Category aggregate and delegating persistence concerns to repositories.
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all categories from the repository.
     *
     * @return A list of categories.
     */
    @Override
    public List<CategoryDto> getAllCategories() {
        logger.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map((element) -> modelMapper.map(element, CategoryDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Fetches a single category by its identifier code.
     *
     * @param code The unique identifier of the category.
     * @return An optional containing the category if found, otherwise empty.
     */
    @Override
    public CategoryDto getCategoryByCode(String code) {
        logger.info("Fetching category by code: {}", code);
        validateCode(code);
        return modelMapper.map(categoryRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("Category with code " + code + " not found")), CategoryDto.class);
    }

    /**
     * Persists a new category or updates an existing one in the repository.
     *
     * @param categoryDto The category data transfer object containing the details to be saved.
     * @return The saved category instance as a DTO.
     */
    @Override
    public CategoryDto saveOrUpdateCategory(CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findByCode(categoryDto.getCode()).orElse(null);

        if (existingCategory == null) {
            logger.info("Creating new category with details: {}", categoryDto);
            return createCategory(categoryDto);
        } else {
            logger.info("Updating existing category with code: {}", categoryDto.getCode());
            return updateCategory(existingCategory, categoryDto);
        }
    }

    private CategoryDto createCategory(CategoryDto categoryDto) {
        categoryDto.setCode(generateUniqueCode(categoryDto));
        validateCategory(categoryDto);
        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    private CategoryDto updateCategory(Category existingCategory, CategoryDto categoryDto) {
        modelMapper.map(categoryDto, existingCategory);
        validateCategory(categoryDto);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    /**
     * Deletes a category by its identifier.
     * This method is transactional, meaning Spring handles the transaction boundaries.
     *
     * @param code The unique identifier code of the category to delete.
     * @throws IllegalArgumentException if the code is invalid or the category does not exist.
     */
    @Override
    @Transactional
    public void deleteCategory(String code) {
        logger.info("Deleting category with code: {}", code);
        validateCode(code);
        categoryRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Category with code " + code + " not found"));
        categoryRepository.deleteByCode(code);
    }

    /**
     * Generates a unique code for a category based on its name and the current timestamp.
     * This method constructs a base string from the category's name and the current system time,
     * hashes this base string using SHA-256 to ensure uniqueness and reduce predictability,
     * and then encodes the hash using URL-safe Base64 encoding. This code can be used as a
     * unique identifier for categories within the system.
     *
     * @param categoryDto The {@link CategoryDto} object for which the unique code is generated.
     *                    The name of the category is used as part of the base string for hash generation.
     * @return A URL-safe Base64-encoded string that represents the unique code for the category.
     *         This code is derived from a SHA-256 hash of the category's name concatenated with the
     *         current system time in milliseconds, ensuring that each code is unique.
     */
    private String generateUniqueCode(CategoryDto categoryDto) {
        String base = categoryDto.getName() + System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex(base);
        return Base64.getUrlEncoder().encodeToString(hash.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates the given category entity.
     *
     * @param categoryDto The categoryDto to validate.
     * @throws IllegalArgumentException if validation fails.
     */
    private void validateCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
    }

    /**
     * Validates the identifier code.
     *
     * @param code The identifier code to validate.
     * @throws IllegalArgumentException if the code is invalid.
     */
    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Invalid category code.");
        }
    }
}
