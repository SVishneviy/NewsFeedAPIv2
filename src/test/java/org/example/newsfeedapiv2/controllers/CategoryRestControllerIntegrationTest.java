package org.example.newsfeedapiv2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.newsfeedapiv2.dto.CategoryDTO;
import org.example.newsfeedapiv2.exceptions.NotFoundException;
import org.example.newsfeedapiv2.services.CategoryServiceImpl;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryRestController.class)
public class CategoryRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryServiceImpl categoryService;
    private static CategoryDTO sampleCategoryDTO;

    @BeforeAll
    public static void setUp() {
        sampleCategoryDTO = new CategoryDTO()
                .setId(1L)
                .setTitle("Title");
    }

    @Test
    @DisplayName("Create category - Created")
    public void testCreateCategory_Created() throws Exception {
        when(categoryService.create(any(CategoryDTO.class))).thenReturn(sampleCategoryDTO);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleCategoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(sampleCategoryDTO.getId().intValue())))
                .andExpect(jsonPath("$.title", is(sampleCategoryDTO.getTitle())));

        verify(categoryService, times(1)).create(any(CategoryDTO.class));
    }

    @Nested
    @DisplayName("Successful scenarios")
    class SuccessfulScenarios {

        @Test
        @DisplayName("Get all categories - Success")
        public void testGetAllCategories_Success() throws Exception {
            List<CategoryDTO> categoryDTOList = Collections.singletonList(sampleCategoryDTO);

            given(categoryService.getAll()).willReturn(categoryDTOList);

            mockMvc.perform(get("/api/categories", status().isOk()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id", is(sampleCategoryDTO.getId().intValue())))
                    .andExpect(jsonPath("$[0].title", is(sampleCategoryDTO.getTitle())));

            verify(categoryService, times(1)).getAll();
        }

        @Test
        @DisplayName("Get category by ID - Success")
        public void testGetCategoryById_Success() throws Exception {
            given(categoryService.getById(sampleCategoryDTO.getId())).willReturn(sampleCategoryDTO);

            mockMvc.perform(get("/api/categories/{id}", sampleCategoryDTO.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(sampleCategoryDTO.getId().intValue())))
                    .andExpect(jsonPath("$.title", is(sampleCategoryDTO.getTitle())));

            verify(categoryService, times(1)).getById(sampleCategoryDTO.getId());
        }

        @Test
        @DisplayName("Update category - Success")
        public void testUpdateCategory_Success() throws Exception {
            when(categoryService.update(any(CategoryDTO.class))).thenReturn(sampleCategoryDTO);

            mockMvc.perform(put("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(sampleCategoryDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(sampleCategoryDTO.getId().intValue())))
                    .andExpect(jsonPath("$.title", is(sampleCategoryDTO.getTitle())));

            verify(categoryService, times(1)).update(any(CategoryDTO.class));
        }

        @Test
        @DisplayName("Delete category - Success")
        public void testDeleteCategory_Success() throws Exception {
            mockMvc.perform(delete("/api/categories/{id}", sampleCategoryDTO.getId()))
                    .andExpect(status().isOk());

            verify(categoryService, times(1)).delete(sampleCategoryDTO.getId());
        }

    }

    @Nested
    @DisplayName("Not found scenarios")
    class NotFoundScenarios {

        @Test
        @DisplayName("Get category by ID - No Found")
        public void testGetCategoryById_NotFound() throws Exception {
            when(categoryService.getById(sampleCategoryDTO.getId())).thenThrow(NotFoundException.class);

            mockMvc.perform(get("/api/categories/{id}", sampleCategoryDTO.getId()))
                    .andExpect(status().isNotFound());

            verify(categoryService, times(1)).getById(sampleCategoryDTO.getId());
        }

        @Test
        @DisplayName("Update category - Not Found")
        public void testUpdateCategory_NotFound() throws Exception {
            when(categoryService.create(sampleCategoryDTO)).thenThrow(NotFoundException.class);

            mockMvc.perform(post("/api/categories", status().isNotFound()));

            verify(categoryService, never()).create(any(CategoryDTO.class));
        }

        @Test
        @DisplayName("Delete category - Not Found")
        public void testDeleteCategory_NotFound() throws Exception {
            doThrow(NotFoundException.class).when(categoryService).delete(sampleCategoryDTO.getId());

            mockMvc.perform(delete("/api/categories/{id}", sampleCategoryDTO.getId()))
                    .andExpect(status().isNotFound());

            verify(categoryService, times(1)).delete(sampleCategoryDTO.getId());
        }

    }

    private String asJsonString(CategoryDTO categoryDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(categoryDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}