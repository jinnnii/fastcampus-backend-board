package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.Comment;
import com.fastcampus.backendboard.domain.UserAccount;
import java.time.LocalDateTime;


public record CommentDto(
        Long id,
        Long articleId,
        Long parentCommentId,
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
                      Long parentCommentId,
                      String content,
                      LocalDateTime createdAt,
                      String createdId,
                      LocalDateTime modifiedAt,
                      String modifiedId,
                      UserAccountDto userAccountDto) {
        return new CommentDto(id, articleId, parentCommentId, content, createdAt, createdId, modifiedAt, modifiedId, userAccountDto);
    }

    public static CommentDto of(Long articleId, String content, UserAccountDto userAccountDto){
        return CommentDto.of(articleId, null, content, userAccountDto);
    }

    public static CommentDto of(Long articleId, Long parentCommentId, String content, UserAccountDto userAccountDto){
        return CommentDto.of(null, articleId, parentCommentId, content, null, null, null, null, userAccountDto);
    }

    public static CommentDto from(Comment entity){
        return new CommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                entity.getParentCommentId(),
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

    public Comment toEntity(Article article, UserAccount userAccount) {
        return  Comment.of(userAccount, article, content);
    }
}
