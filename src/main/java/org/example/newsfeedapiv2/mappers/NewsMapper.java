package org.example.newsfeedapiv2.mappers;

import org.example.newsfeedapiv2.dto.NewsDTO;
import org.example.newsfeedapiv2.entity.Category;
import org.example.newsfeedapiv2.entity.News;
import org.mapstruct.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {
    News mapToEntity(NewsDTO newsDTO);

    @Mapping(target = "categoryTitle", source = "news.category", qualifiedByName = "extractCategoryTitle")
    @Mapping(target = "date", source = "news.date", qualifiedByName = "truncateToSeconds")
    NewsDTO mapToDTO(News news);

    @Named("extractCategoryTitle")
    default String extractCategoryTitle(Category category) {
        return category != null ? category.getTitle() : null;
    }

    @Named("truncateToSeconds")
    default Instant truncateToSeconds(Instant date) {
        return date.truncatedTo(ChronoUnit.SECONDS);
    }
}