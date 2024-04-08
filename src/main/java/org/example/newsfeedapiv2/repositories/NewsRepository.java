package org.example.newsfeedapiv2.repositories;

import org.example.newsfeedapiv2.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Collection<News> findByCategoryId(Long categoryId);
}