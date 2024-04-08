package org.example.newsfeedapiv2.repositories;

import org.example.newsfeedapiv2.entity.Category;
import org.example.newsfeedapiv2.entity.News;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class NewsRepositoryIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;
    private News sampleNews;

    @BeforeEach
    public void SetUp() {
        Category category = new Category().setTitle("Title");
        categoryRepository.save(category);

        sampleNews = new News()
                .setTitle("Title")
                .setText("Text")
                .setCategory(category);
        newsRepository.save(sampleNews);
    }

    @AfterEach
    public void tearDown() {
        newsRepository.delete(sampleNews);
    }

    @Test
    @DisplayName("Find all news")
    public void testFindAllNews() {
        List<News> expectedNewsList = Collections.singletonList(sampleNews);
        List<News> foundNewsList = newsRepository.findAll();

        assertThat(foundNewsList)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedNewsList);
    }

    @Test
    @DisplayName("Find news by ID")
    public void testFindNewsById() {
        News foundNews = newsRepository.findById(sampleNews.getId()).orElseThrow();

        assertThat(foundNews)
                .isNotNull()
                .isEqualTo(sampleNews);
    }

    @Test
    @DisplayName("Find news by category ID")
    public void testFindNewsByCategoryId() {
        entityManager.clear();

        Category foundCategory = entityManager.find(Category.class, sampleNews.getCategory().getId());

        assertThat(foundCategory).isNotNull();

        List<News> foundNewsList = foundCategory.getNewsList();

        assertThat(foundNewsList).isNotNull().hasSize(1);
        assertThat(foundNewsList.get(0).getId()).isEqualTo(sampleNews.getId());
        assertThat(foundNewsList.get(0).getTitle()).isEqualTo(sampleNews.getTitle());
        assertThat(foundNewsList.get(0).getDate()).isEqualTo(sampleNews.getDate());
    }

    @Test
    @DisplayName("Update news")
    public void testUpdateNews() {
        sampleNews.setTitle("New title");

        News updatedNews = newsRepository.save(sampleNews);

        assertThat(updatedNews).isNotNull();
        assertThat(updatedNews).isEqualTo(sampleNews);

        News foundNews = newsRepository.findById(sampleNews.getId()).orElseThrow();

        assertThat(foundNews)
                .isNotNull()
                .isEqualTo(sampleNews);
    }

    @Test
    @DisplayName("Delete news by ID")
    public void testDeleteNewsById() {
        newsRepository.deleteById(sampleNews.getId());

        boolean isNewsDeleted = newsRepository.existsById(sampleNews.getId());

        assertThat(isNewsDeleted).isFalse();
    }

}