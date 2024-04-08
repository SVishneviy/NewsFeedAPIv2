package org.example.newsfeedapiv2.services;

import org.example.newsfeedapiv2.dto.CategoryDTO;
import org.example.newsfeedapiv2.dto.NewsDTO;
import org.example.newsfeedapiv2.entity.Category;
import org.example.newsfeedapiv2.exceptions.NotFoundException;
import org.example.newsfeedapiv2.mappers.CategoryMapper;
import org.example.newsfeedapiv2.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CategoryServiceImplIntegrationTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @MockBean
    private CategoryRepository categoryRepository;
    private static CategoryDTO sampleCategoryDTO;

    @BeforeAll
    public static void setUp() {
        NewsDTO news = new NewsDTO()
                .setId(1L)
                .setTitle("Title")
                .setText("Text")
                .setDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        sampleCategoryDTO = new CategoryDTO()
                .setId(1L)
                .setTitle("Title")
                .setNewsList(Collections.singletonList(news));
    }

    @Nested
    @DisplayName("Successful scenarios")
    class SuccessfulScenarios {

        @Test
        @DisplayName("Get all categories - Success")
        public void testGelAllCategories_Success() {
            Collection<CategoryDTO> expectedCategoryDTOList = Collections.singletonList(sampleCategoryDTO);

            when(categoryRepository.findAll()).thenReturn(expectedCategoryDTOList.stream()
                    .map(categoryMapper::mapToEntity)
                    .toList()
            );

            Collection<CategoryDTO> foundCategoryDTOList = categoryService.getAll();

            assertThat(foundCategoryDTOList)
                    .isNotNull()
                    .isNotEmpty()
                    .isEqualTo(expectedCategoryDTOList);

            verify(categoryRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Get category by ID - Success")
        public void testGetCategoryById_Success() {
            Category category = categoryMapper.mapToEntity(sampleCategoryDTO);

            when(categoryRepository.findById(sampleCategoryDTO.getId()))
                    .thenReturn(Optional.of(category));

            CategoryDTO foundCategoryDTO = categoryService.getById(sampleCategoryDTO.getId());

            assertThat(foundCategoryDTO)
                    .isNotNull()
                    .isEqualTo(sampleCategoryDTO);

            verify(categoryRepository, times(1)).findById(sampleCategoryDTO.getId());
        }

        @Test
        @DisplayName("Create category - Success")
        public void testCreateCategory_Success() {
            Category category = categoryMapper.mapToEntity(sampleCategoryDTO);

            when(categoryRepository.save(category)).thenReturn(category);

            CategoryDTO createdCategoryDTO = categoryService.create(sampleCategoryDTO);

            assertThat(createdCategoryDTO)
                    .isNotNull()
                    .isEqualTo(sampleCategoryDTO);

            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @DisplayName("Update category - Success")
        public void testUpdateCategory_Success() {
            Category category = categoryMapper.mapToEntity(sampleCategoryDTO);

            when(categoryRepository.findById(sampleCategoryDTO.getId())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            CategoryDTO updatedCategoryDTO = categoryService.update(sampleCategoryDTO);

            assertThat(updatedCategoryDTO)
                    .isNotNull()
                    .isEqualTo(sampleCategoryDTO);

            verify(categoryRepository, times(1)).findById(sampleCategoryDTO.getId());
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @DisplayName("Delete category by ID - Success")
        public void deleteCategoryById_Success() {
            Category category = categoryMapper.mapToEntity(sampleCategoryDTO);

            when(categoryRepository.findById(sampleCategoryDTO.getId())).thenReturn(Optional.of(category));

            categoryService.delete(sampleCategoryDTO.getId());

            boolean isCategoryDeleted = categoryRepository.existsById(sampleCategoryDTO.getId());

            assertThat(isCategoryDeleted).isFalse();

            verify(categoryRepository, times(1)).deleteById(sampleCategoryDTO.getId());
        }

        @Test
        @DisplayName("Get category by title - Success")
        public void getCategoryByTitle_Success() {
            Category category = categoryMapper.mapToEntity(sampleCategoryDTO);

            when(categoryRepository.findByTitle(sampleCategoryDTO.getTitle())).thenReturn(Optional.of(category));

            Category foundCategory = categoryService.getCategoryByTitleOrThrow(sampleCategoryDTO.getTitle());

            assertThat(foundCategory)
                    .isNotNull()
                    .isEqualTo(category);

            verify(categoryRepository, times(1)).findByTitle(sampleCategoryDTO.getTitle());
        }

    }

    @Nested
    @DisplayName("Not found scenarios")
    class NotFoundScenarios {

        @Test
        @DisplayName("Get category by ID - Not Found")
        public void testGetCategoryById_NotFound() {
            when(categoryRepository.findById(sampleCategoryDTO.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> categoryService.getById(sampleCategoryDTO.getId()));
        }

        @Test
        @DisplayName("Get category by title - Not Found")
        void getCategoryByTitle_NotFound() {
            when(categoryRepository.findByTitle(sampleCategoryDTO.getTitle())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> categoryService.getCategoryByTitleOrThrow(sampleCategoryDTO.getTitle()));
        }

        @Test
        @DisplayName("Delete category by ID - Not Found")
        public void deleteCategoryById_NotFound() {
            when(categoryRepository.findById(sampleCategoryDTO.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> categoryService.delete(sampleCategoryDTO.getId()));
        }

    }

}