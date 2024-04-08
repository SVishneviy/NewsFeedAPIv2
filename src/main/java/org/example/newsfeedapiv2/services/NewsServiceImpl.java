package org.example.newsfeedapiv2.services;

import lombok.RequiredArgsConstructor;
import org.example.newsfeedapiv2.dto.NewsDTO;
import org.example.newsfeedapiv2.entity.Category;
import org.example.newsfeedapiv2.entity.News;
import org.example.newsfeedapiv2.exceptions.NotFoundException;
import org.example.newsfeedapiv2.mappers.NewsMapper;
import org.example.newsfeedapiv2.repositories.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NewsServiceImpl implements CRUDService<NewsDTO> {

    private final NewsMapper mapper;
    private final NewsRepository repository;
    private final CategoryServiceImpl categoryService;

    @Override
    public Collection<NewsDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public NewsDTO getById(Long id) {
        return mapToDTO(getNewsByIdOrThrow(id));
    }

    public List<NewsDTO> getByCategoryId(Long id) {
        if (!categoryService.existsById(id)) {
            throw new NotFoundException("Category not found with id: " + id);
        }
        return repository.findByCategoryId(id).stream()
                .map(this::mapToDTO)
                .toList();

    }

    @Override
    public NewsDTO create(NewsDTO newsDTO) {
        News news = mapToEntity(newsDTO);
        return mapToDTO(repository.save(news));
    }

    @Override
    public NewsDTO update(NewsDTO newsDTO) {
        News existingNews = getNewsByIdOrThrow(newsDTO.getId());

        Category category = categoryService
                .getCategoryByTitleOrThrow(newsDTO.getCategoryTitle());

        existingNews.setTitle(newsDTO.getTitle());
        existingNews.setText(newsDTO.getText());
        existingNews.setCategory(category);

        return mapToDTO(repository.save(existingNews));
    }

    @Override
    public void delete(Long id) {
        getNewsByIdOrThrow(id);
        repository.deleteById(id);
    }

    public News getNewsByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
    }

    public News mapToEntity(NewsDTO newsDTO) {
        Category category = categoryService
                .getCategoryByTitleOrThrow(newsDTO.getCategoryTitle());

        News news = mapper.mapToEntity(newsDTO);
        news.setCategory(category);
        return news;
    }

    public NewsDTO mapToDTO(News news) {
        return mapper.mapToDTO(news);
    }

}