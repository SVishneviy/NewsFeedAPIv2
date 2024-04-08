package org.example.newsfeedapiv2.services;

import org.example.newsfeedapiv2.dto.NewsDTO;
import org.example.newsfeedapiv2.entity.Category;
import org.example.newsfeedapiv2.entity.News;
import org.example.newsfeedapiv2.exceptions.NotFoundException;
import org.example.newsfeedapiv2.mappers.NewsMapper;
import org.example.newsfeedapiv2.repositories.CategoryRepository;
import org.example.newsfeedapiv2.repositories.NewsRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class NewsServiceImplIntegrationTest {

    @Autowired
    private NewsServiceImpl newsService;

    @Autowired
    private NewsMapper newsMapper;

    @MockBean
    private NewsRepository newsRepository;

    @MockBean
    private CategoryRepository categoryRepository;
    private static NewsDTO sampleNewsDTO;
    private static Category sampleCategory;

    @BeforeAll
    public static void setUp() {
        sampleNewsDTO = new NewsDTO()
                .setId(1L)
                .setTitle("Title")
                .setText("Text")
                .setDate(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setCategoryTitle("Category title");

        sampleCategory = new Category()
                .setId(1L)
                .setTitle(sampleNewsDTO.getCategoryTitle());
    }

    @Nested
    @DisplayName("Successful scenarios")
    class SuccessfulScenarios {

        @Test
        @DisplayName("Get all news - Success")
        public void testGelAllNews_Success() {
            Collection<NewsDTO> expectedNewsDTOList = Collections.singletonList(sampleNewsDTO);

            when(newsRepository.findAll()).thenReturn(expectedNewsDTOList.stream()
                    .map(n -> {
                                News news = newsMapper.mapToEntity(n);
                                news.setCategory(sampleCategory);
                                return news;
                    })
                    .toList()
            );

            Collection<NewsDTO> foundNewsDTOList = newsService.getAll();

            assertThat(foundNewsDTOList)
                    .isNotNull()
                    .isNotEmpty()
                    .isEqualTo(expectedNewsDTOList);

            verify(newsRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Get news by ID - Success")
        public void testGetNewsById_Success() {
            News news = newsMapper.mapToEntity(sampleNewsDTO).setCategory(sampleCategory);

            when(newsRepository.findById(sampleNewsDTO.getId())).thenReturn(Optional.of(news));

            NewsDTO foundNewsDTO = newsService.getById(sampleNewsDTO.getId());

            assertThat(foundNewsDTO)
                    .isNotNull()
                    .isEqualTo(sampleNewsDTO);

            verify(newsRepository, times(1)).findById(sampleNewsDTO.getId());
        }

        @Test
        @DisplayName("Get news by Category ID - Success")
        public void testGetNewsByCategoryId_Success() {
            Collection<NewsDTO> expectedNewsDTOList = Collections.singletonList(sampleNewsDTO);

            News news = newsMapper.mapToEntity(sampleNewsDTO).setCategory(sampleCategory);
            sampleCategory.setNewsList(Collections.singletonList(news));

            when(categoryRepository.existsById(sampleCategory.getId())).thenReturn(true);
            when(newsRepository.findByCategoryId(sampleCategory.getId()))
                    .thenReturn(expectedNewsDTOList.stream()
                            .map(n -> {
                                News news1 = newsMapper.mapToEntity(n);
                                news1.setCategory(sampleCategory);
                                return news1;
                            })
                            .toList());

            List<NewsDTO> foundNewsDTOList = newsService.getByCategoryId(sampleCategory.getId());

            assertThat(foundNewsDTOList)
                    .isNotNull()
                    .isNotEmpty()
                    .isEqualTo(expectedNewsDTOList);

            verify(categoryRepository, times(1)).existsById(sampleCategory.getId());
            verify(newsRepository, times(1)).findByCategoryId(sampleCategory.getId());
        }

        @Test
        @DisplayName("Create news - Success")
        public void testCreateNews_Success() {
            News news = newsMapper.mapToEntity(sampleNewsDTO).setCategory(sampleCategory);

            when(categoryRepository.findByTitle(sampleCategory.getTitle())).thenReturn(Optional.of(sampleCategory));
            when(newsRepository.save(news)).thenReturn(news);

            NewsDTO createdNewsDTO = newsService.create(sampleNewsDTO);

            assertThat(createdNewsDTO)
                    .isNotNull()
                    .isEqualTo(sampleNewsDTO);

            verify(categoryRepository, times(1)).findByTitle(sampleCategory.getTitle());
            verify(newsRepository, times(1)).save(news);
        }

        @Test
        @DisplayName("Update news - Success")
        public void testUpdateNews_Success() {
            News news = newsMapper.mapToEntity(sampleNewsDTO).setCategory(sampleCategory);

            when(newsRepository.findById(sampleNewsDTO.getId())).thenReturn(Optional.of(news));
            when(categoryRepository.findByTitle(sampleCategory.getTitle())).thenReturn(Optional.of(sampleCategory));
            when(newsRepository.save(any(News.class))).thenReturn(news);

            NewsDTO updatedNewsDTO = newsService.update(sampleNewsDTO);

            assertThat(updatedNewsDTO)
                    .isNotNull()
                    .isEqualTo(sampleNewsDTO);

            verify(newsRepository, times(1)).findById(sampleNewsDTO.getId());
            verify(categoryRepository, times(1)).findByTitle(sampleCategory.getTitle());
            verify(newsRepository, times(1)).save(news);
        }

        @Test
        @DisplayName("Delete news by ID - Success")
        public void deleteNewsById_Success() {
            News news = newsMapper.mapToEntity(sampleNewsDTO);

            when(newsRepository.findById(sampleNewsDTO.getId())).thenReturn(Optional.of(news));

            newsService.delete(sampleNewsDTO.getId());

            boolean isNewsDeleted = newsRepository.existsById(sampleNewsDTO.getId());

            assertThat(isNewsDeleted).isFalse();

            verify(newsRepository, times(1)).deleteById(sampleNewsDTO.getId());
        }

    }

    @Nested
    @DisplayName("Not found scenarios")
    class NotFoundScenarios {

        @Test
        @DisplayName("Get news by ID - Not Found")
        public void testGetNewsById_NotFound() {
            when(newsRepository.findById(sampleNewsDTO.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> newsService.getById(sampleNewsDTO.getId()));

            verify(newsRepository, times(1)).findById(sampleNewsDTO.getId());
        }

        @Test
        @DisplayName("Get news by Category ID - Not Found")
        public void testGetNewsByCategoryId_NotFound() {
            when(categoryRepository.existsById(sampleCategory.getId())).thenReturn(false);
            when(newsRepository.findByCategoryId(sampleCategory.getId())).thenReturn(Collections.emptyList());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> newsService.getByCategoryId(sampleCategory.getId()));

            verify(categoryRepository, times(1)).existsById(sampleCategory.getId());
            verify(newsRepository, times(0)).findByCategoryId(sampleCategory.getId());
        }

        @Test
        @DisplayName("Update news - Not Found")
        public void testUpdateNews_NotFound() {
            when(newsRepository.findById(sampleNewsDTO.getId())).thenReturn(Optional.empty());
            when(categoryRepository.findByTitle(sampleNewsDTO.getCategoryTitle())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> newsService.update(sampleNewsDTO));

            verify(newsRepository, times(1)).findById(sampleNewsDTO.getId());
            verify(categoryRepository, times(0)).findByTitle(sampleNewsDTO.getCategoryTitle());
        }

        @Test
        @DisplayName("Delete news by ID - Not Found")
        public void deleteNewsById_NotFound() {
            when(newsRepository.findById(sampleNewsDTO.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> newsService.delete(sampleNewsDTO.getId()));

            verify(newsRepository, times(1)).findById(sampleNewsDTO.getId());
        }

    }

}