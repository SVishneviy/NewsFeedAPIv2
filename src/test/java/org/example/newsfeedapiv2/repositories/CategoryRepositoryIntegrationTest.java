package org.example.newsfeedapiv2.repositories;

import org.example.newsfeedapiv2.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;
    private Category sampleCategory;

    @BeforeEach
    public void setUp() {
        sampleCategory = new Category().setTitle("Title");
        categoryRepository.save(sampleCategory);
    }

    @AfterEach
    public void tearDown() {
        categoryRepository.delete(sampleCategory);
    }

    @Test
    @DisplayName("Find all categories")
    public void testFindAllCategories() {
        List<Category> expectedCategories = Collections.singletonList(sampleCategory);
        List<Category> foundCategories = categoryRepository.findAll();

        assertThat(foundCategories)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedCategories);
    }

    @Test
    @DisplayName("Find category by ID")
    public void testFindCategoryById() {
        Category foundCategory = categoryRepository.findById(sampleCategory.getId()).orElseThrow();

        assertThat(foundCategory)
                .isNotNull()
                .isEqualTo(sampleCategory);
    }

    @Test
    @DisplayName("Find category by title")
    public void testFindCategoryByTitle() {
        Category foundCategory = categoryRepository.findByTitle(sampleCategory.getTitle()).orElseThrow();

        assertThat(foundCategory)
                .isNotNull()
                .isEqualTo(sampleCategory);
    }

    @Test
    @DisplayName("Update category")
    public void testUpdateCategory() {
        sampleCategory.setTitle("New title");

        Category updatedCategory = categoryRepository.save(sampleCategory);

        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory).isEqualTo(sampleCategory);

        Category foundCategory = categoryRepository
                .findById(sampleCategory.getId())
                .orElseThrow();

        assertThat(foundCategory)
                .isNotNull()
                .isEqualTo(updatedCategory);
    }

    @Test
    @DisplayName("Delete category by ID")
    public void testDeleteCategoryById() {
        categoryRepository.deleteById(sampleCategory.getId());

        boolean isCategoryDeleted = categoryRepository.existsById(sampleCategory.getId());

        assertThat(isCategoryDeleted).isFalse();
    }

}