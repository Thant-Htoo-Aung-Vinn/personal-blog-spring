package com.vinn.personalBlog.api.category.service.impl;

import com.vinn.personalBlog.api.category.dto.CategoryDto;
import com.vinn.personalBlog.api.category.model.Category;
import com.vinn.personalBlog.api.category.repository.CategoryRepository;
import com.vinn.personalBlog.api.category.service.CategoryService;

import java.util.*;

import com.vinn.personalBlog.api.exception.EntityNotFoundException;
import com.vinn.personalBlog.api.post.dto.CategoryPostDto;
import com.vinn.personalBlog.api.post.dto.PostDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
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
     * Retrieves a limited list of categories along with their associated posts.
     * This method fetches categories and their posts, ordered by category name
     * and the creation date of the posts, limited to the first 3 results.
     *
     * @return A list of {@link CategoryPostDto} objects, each containing details about a category
     *         and its associated posts.
     */
    @Override
    public List<CategoryPostDto> getAllCategoriesWithPosts() {

        List<Object[]> rawData = categoryRepository.fetchCategoriesWithPostsRaw();

        return groupPostsByCategory(rawData);
    }

    /**
     * Groups raw database rows into a list of CategoryPostDto objects.
     */
    private List<CategoryPostDto> groupPostsByCategory(List<Object[]> rawData) {
        Map<Long, CategoryPostDto> categoryMap = new LinkedHashMap<>();

        for (Object[] row : rawData) {
            processRowIntoCategoryMap(row, categoryMap);
        }

        return new ArrayList<>(categoryMap.values());
    }

    /**
     * Processes a single row of raw data and updates the category map.
     */
    private void processRowIntoCategoryMap(Object[] row, Map<Long, CategoryPostDto> categoryMap) {
        Long categoryId = (Long) row[0];
        String categoryName = (String) row[1];
        String categoryDescription = (String) row[2];
        String iconName = (String) row[3];
        Long postId = row[4] != null ? ((Number) row[4]).longValue() : null;
        String postTitle = (String) row[5];
        String postDescription = (String) row[6];
        String categoryCode = (String) row[7];

        CategoryPostDto categoryPostDto = categoryMap.computeIfAbsent(categoryId, id ->
                createCategoryPostDto(categoryId, categoryName, categoryDescription, iconName));

        if (postId != null) {
            categoryPostDto.getPosts().add(createPostDto(postId, postTitle, postDescription, categoryCode));
        }
    }

    /**
     * Creates a CategoryPostDto for a given category.
     */
    private CategoryPostDto createCategoryPostDto(Long categoryId, String name, String description, String iconName) {
        return new CategoryPostDto(categoryId, name, description, iconName, new ArrayList<>());
    }

    /**
     * Creates a PostDto for a given post.
     */
    private PostDto createPostDto(Long postId, String title, String description, String categoryCode) {
        return new PostDto(postId, title, description, categoryCode);
    }

    /**
     * Retrieves posts associated with a specific category, identified by its name.
     * This method fetches posts belonging to the given category, ordered by their creation date
     * in descending order.
     *
     * @param categoryName The name of the category whose posts are to be retrieved.
     *                     Must be a valid, non-null, and non-empty string.
     * @return An object of {@link CategoryPostDto}, containing details about the category
     * and its associated posts.
     * @throws IllegalArgumentException if the provided category name is invalid.
     * @throws EntityNotFoundException  if no category with the given name is found.
     */
    @Override
    public CategoryPostDto getPostsByCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            logger.error("Invalid category name provided: '{}'. Category name must be non-null and non-empty.", categoryName);
            throw new IllegalArgumentException("Category name must be a valid, non-null, and non-empty string.");
        }

        List<PostDto> posts = categoryRepository.fetchPostsByCategoryName(categoryName);

        if (posts == null || posts.isEmpty()) {
            logger.warn("No posts found for category: '{}'.", categoryName);
            throw new EntityNotFoundException("No posts found for category: " + categoryName);
        }

        Category category = categoryRepository.findByName(categoryName);

        return new CategoryPostDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIconName(),
                posts
        );
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
