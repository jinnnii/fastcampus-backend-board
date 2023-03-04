package com.fastcampus.backendboard.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Article} entity
 */
public record ArticleDto(
        LocalDateTime createdAt,
        String createdId,
        String title,
        String content,
        String hashtag
)  {
    public static ArticleDto of(LocalDateTime createdAt, String createdId, String title, String content, String hashtag) {
        return new ArticleDto(createdAt, createdId, title, content, hashtag);
    }


}