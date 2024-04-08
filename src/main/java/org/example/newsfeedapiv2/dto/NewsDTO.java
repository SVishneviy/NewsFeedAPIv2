package org.example.newsfeedapiv2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Accessors(chain = true, fluent = false)
@Data
public class NewsDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("text")
    private String text;

    @JsonProperty("date")
    private Instant date;

    @JsonProperty("category")
    private String categoryTitle;

}