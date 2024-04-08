package org.example.newsfeedapiv2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.newsfeedapiv2.dto.NewsDTO;
import org.example.newsfeedapiv2.exceptions.NotFoundException;
import org.example.newsfeedapiv2.services.NewsServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@ExtendWith(SpringExtension.class)
@WebMvcTest(NewsRestController.class)
public class NewsRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsServiceImpl newsService;

    private static NewsDTO sampleNewsDTO;

    @BeforeAll
    public static void setUp() {
        sampleNewsDTO = new NewsDTO()
                .setId(1L)
                .setTitle("Title")
                .setText("Text")
                .setDate(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setCategoryTitle("Category title");
    }

    @Test
    @DisplayName("Create news - Created")
    public void testCreateNews_Created() throws Exception {
        when(newsService.create(any(NewsDTO.class))).thenReturn(sampleNewsDTO);

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleNewsDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(sampleNewsDTO.getId().intValue())))
                .andExpect(jsonPath("$.title", is(sampleNewsDTO.getTitle())))
                .andExpect(jsonPath("$.text", is(sampleNewsDTO.getText())))
                .andExpect(jsonPath("$.category", is(sampleNewsDTO.getCategoryTitle())));

        verify(newsService, times(1)).create(any(NewsDTO.class));
    }

    @Nested
    @DisplayName("Successful scenarios")
    class SuccessfulScenarios {

        @Test
        @DisplayName("Get all news - Success")
        public void testGetAllNews_Success() throws Exception {
            List<NewsDTO> newsDTOList = Collections.singletonList(sampleNewsDTO);

            given(newsService.getAll()).willReturn(newsDTOList);

            mockMvc.perform(get("/api/news", status().isOk()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id", is(sampleNewsDTO.getId().intValue())))
                    .andExpect(jsonPath("$[0].title", is(sampleNewsDTO.getTitle())))
                    .andExpect(jsonPath("$[0].text", is(sampleNewsDTO.getText())))
                    .andExpect(jsonPath("$[0].date", is(sampleNewsDTO.getDate().toString())))
                    .andExpect(jsonPath("$[0].category", is(sampleNewsDTO.getCategoryTitle())));

            verify(newsService, times(1)).getAll();
        }

        @Test
        @DisplayName("Get news by ID - Success")
        public void testGetNewsById_Success() throws Exception {
            given(newsService.getById(sampleNewsDTO.getId())).willReturn(sampleNewsDTO);

            mockMvc.perform(get("/api/news/{id}", sampleNewsDTO.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(sampleNewsDTO.getId().intValue())))
                    .andExpect(jsonPath("$.title", is(sampleNewsDTO.getTitle())))
                    .andExpect(jsonPath("$.text", is(sampleNewsDTO.getText())))
                    .andExpect(jsonPath("$.date", is(sampleNewsDTO.getDate().toString())))
                    .andExpect(jsonPath("$.category", is(sampleNewsDTO.getCategoryTitle())));

            verify(newsService, times(1)).getById(sampleNewsDTO.getId());
        }

        @Test
        @DisplayName("Get news by category ID - Success")
        public void testGetNewsByCategoryId_Success() throws Exception {
            Long categoryID = 1L;

            List<NewsDTO> newsDTOList = Collections.singletonList(sampleNewsDTO);

            given(newsService.getByCategoryId(categoryID)).willReturn(newsDTOList);

            mockMvc.perform(get("/api/news/category/{id}", categoryID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id", is(sampleNewsDTO.getId().intValue())))
                    .andExpect(jsonPath("$[0].title", is(sampleNewsDTO.getTitle())))
                    .andExpect(jsonPath("$[0].text", is(sampleNewsDTO.getText())))
                    .andExpect(jsonPath("$[0].date", is(sampleNewsDTO.getDate().toString())))
                    .andExpect(jsonPath("$[0].category", is(sampleNewsDTO.getCategoryTitle())));

            verify(newsService, times(1)).getByCategoryId(categoryID);
        }

        @Test
        @DisplayName("Update news - Success")
        public void testUpdateNews_Success() throws Exception {
            when(newsService.update(any(NewsDTO.class))).thenReturn(sampleNewsDTO);

            mockMvc.perform(put("/api/news")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(sampleNewsDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(sampleNewsDTO.getId().intValue())))
                    .andExpect(jsonPath("$.title", is(sampleNewsDTO.getTitle())))
                    .andExpect(jsonPath("$.text", is(sampleNewsDTO.getText())))
                    .andExpect(jsonPath("$.date", is(sampleNewsDTO.getDate().toString())))
                    .andExpect(jsonPath("$.category", is(sampleNewsDTO.getCategoryTitle())));

            verify(newsService, times(1)).update(sampleNewsDTO);
        }

        @Test
        @DisplayName("Delete news - Success")
        public void testDeleteNews_Success() throws Exception {
            mockMvc.perform(delete("/api/news/{id}", sampleNewsDTO.getId()))
                    .andExpect(status().isOk());

            verify(newsService, times(1)).delete(sampleNewsDTO.getId());
        }

    }

    @Nested
    @DisplayName("Not found scenarios")
    class NotFoundScenarios {

        @Test
        @DisplayName("Get news by ID - No Found")
        public void testGetNewsById_NotFound() throws Exception {
            doThrow(NotFoundException.class).when(newsService).getById(sampleNewsDTO.getId());

            mockMvc.perform(get("/api/news/{id}", sampleNewsDTO.getId()))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1)).getById(sampleNewsDTO.getId());
        }

        @Test
        @DisplayName("Get news by category ID - Not Found")
        public void testGetNewsByCategoryId_NotFound() throws Exception {
            Long categoryID = 1L;

            doThrow(NotFoundException.class).when(newsService).getByCategoryId(categoryID);

            mockMvc.perform(get("/api/news/category/{id}", categoryID))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1)).getByCategoryId(categoryID);
        }

        @Test
        @DisplayName("Create news - Not Found")
        public void testCreateNews_NotFound() throws Exception {
            when(newsService.create(sampleNewsDTO)).thenThrow(NotFoundException.class);

            mockMvc.perform(post("/api/news", status().isNotFound()));

            verify(newsService, never()).create(sampleNewsDTO);
        }

        @Test
        @DisplayName("Update news - Not Found")
        public void testUpdateNews_NotFound() throws Exception {
            when(newsService.update(any(NewsDTO.class))).thenThrow(NotFoundException.class);

            mockMvc.perform(post("/api/news", status().isNotFound()));

            verify(newsService, never()).update(any(NewsDTO.class));
        }

        @Test
        @DisplayName("Delete news - Not Found")
        public void testDeleteNews_Success() throws Exception {
            doThrow(NotFoundException.class).when(newsService).delete(sampleNewsDTO.getId());

            mockMvc.perform(delete("/api/news/{id}", sampleNewsDTO.getId()))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1)).delete(sampleNewsDTO.getId());
        }

    }

    private String asJsonString(NewsDTO newsDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(newsDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}