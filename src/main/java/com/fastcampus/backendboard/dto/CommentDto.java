
package com.fastcampus.backendboard.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Comment} entity
 */
public record CommentDto(
        LocalDateTime createdAt,
        String createdId,
        String content
) {
    public static CommentDto of(LocalDateTime createdAt, String createdId, String content) {
        return new CommentDto(createdAt,createdId,content);
    }
}