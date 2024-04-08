package org.example.newsfeedapiv2.services;

import lombok.RequiredArgsConstructor;
import org.example.newsfeedapiv2.dto.CategoryDTO;
import org.example.newsfeedapiv2.entity.Category;
import org.example.newsfeedapiv2.exceptions.NotFoundException;
import org.example.newsfeedapiv2.mappers.CategoryMapper;
import org.example.newsfeedapiv2.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CRUDService<CategoryDTO> {

    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public Collection<CategoryDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public CategoryDTO getById(Long id) {
            return mapToDTO(getCategoryByIdOrThrow(id));
    }

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        Category category = mapToEntity(categoryDTO);
        return mapToDTO(repository.save(category));
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryByIdOrThrow(categoryDTO.getId());
        existingCategory.setTitle(categoryDTO.getTitle());
        return mapToDTO(repository.save(existingCategory));
    }

    @Override
    public void delete(Long id) {
        getCategoryByIdOrThrow(id);
        repository.deleteById(id);
    }

    public Category getCategoryByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

    public Category getCategoryByTitleOrThrow(String title) {
        return repository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException("Category not found with title: " + title));
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Category mapToEntity(CategoryDTO categoryDTO) {
        return mapper.mapToEntity(categoryDTO);
    }

    public CategoryDTO mapToDTO(Category category) {
        return mapper.mapToDTO(category);
    }

}