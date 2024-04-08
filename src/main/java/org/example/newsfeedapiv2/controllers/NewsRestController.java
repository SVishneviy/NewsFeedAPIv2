package org.example.newsfeedapiv2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.newsfeedapiv2.dto.NewsDTO;
import org.example.newsfeedapiv2.services.NewsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class NewsRestController {

    private final NewsServiceImpl newsService;

    @GetMapping
    public ResponseEntity<Collection<NewsDTO>> getAll() {
        return ResponseEntity.ok(newsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getById(id));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<NewsDTO>> getByCategoryId(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getByCategoryId(id));
    }

    @PostMapping
    public ResponseEntity<NewsDTO> create(@RequestBody NewsDTO newsDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newsService.create(newsDTO));
    }

    @PutMapping
    public ResponseEntity<NewsDTO> update(@RequestBody NewsDTO newsDTO) {
        return ResponseEntity.ok(newsService.update(newsDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.ok().build();
    }

}