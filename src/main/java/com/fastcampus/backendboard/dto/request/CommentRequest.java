package com.fastcampus.backendboard.dto.request;

import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.dto.UserAccountDto;

import java.io.Serializable;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Comment} entity
 */
public record CommentRequest(
        Long articleId,
        String content
) {
    public static CommentRequest of (Long articleId, String content) {
        return new CommentRequest(articleId, content);
    }

    public CommentDto toDto(UserAccountDto userAccountDto){
        return CommentDto.of(articleId, content, userAccountDto);
    }
}