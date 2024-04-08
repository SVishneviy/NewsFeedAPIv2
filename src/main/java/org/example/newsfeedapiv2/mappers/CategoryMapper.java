package org.example.newsfeedapiv2.mappers;

import org.example.newsfeedapiv2.dto.CategoryDTO;
import org.example.newsfeedapiv2.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = NewsMapper.class)
public interface CategoryMapper {
    Category mapToEntity(CategoryDTO categoryDTO);
    CategoryDTO mapToDTO(Category category);
}