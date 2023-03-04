package com.fastcampus.backendboard.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Article} entity
 */
public record ArticleUpdateDto(
        String title,
        String content,
        String hashtag
){
    public static ArticleUpdateDto of (String title, String content, String hashtag) {
        return new ArticleUpdateDto(title, content, hashtag);
    }
}