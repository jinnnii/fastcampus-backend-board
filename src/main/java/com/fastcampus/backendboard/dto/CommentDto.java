package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Comment} entity
 */
public record CommentDto(
        Long id,
        Long articleId,
        String content,
        LocalDateTime createdAt,
        String createdId,
        LocalDateTime modifiedAt,
        String modifiedId,
        UserAccountDto userAccountDto
) {
    public static CommentDto of(
                      Long id,
                      Long articleId,
                      String content,
                      LocalDateTime createdAt,
                      String createdId,
                      LocalDateTime modifiedAt,
                      String modifiedId,
                      UserAccountDto userAccountDto) {
        return new CommentDto(id, articleId, content, createdAt, createdId, modifiedAt, modifiedId, userAccountDto);
    }

    public static CommentDto from(Comment entity){
        return new CommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedId(),
                entity.getModifiedAt(),
                entity.getModifiedId(),
                UserAccountDto.from(entity.getUserAccount())
        );
    }

    public Comment toEntity(Article article){
        return Comment.of(userAccountDto.toEntity(), article, content);
    }
}