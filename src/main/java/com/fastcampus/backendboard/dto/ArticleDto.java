package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.UserAccount;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Article} entity
 */
public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdId,
        LocalDateTime modifiedAt,
        String modifiedId

) {
    public static ArticleDto of(Long id,
                                UserAccountDto userAccountDto,
                                String title,
                                String content,
                                String hashtag,
                                LocalDateTime createdAt,
                                String createdId,
                                LocalDateTime modifiedAt,
                                String modifiedId) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdId, modifiedAt, modifiedId);
    }
    public static ArticleDto from(Article entity)
    {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedId(),
                entity.getModifiedAt(),
                entity.getModifiedId()
        );
    }

    public Article toEntity(){
        return Article.of(userAccountDto.toEntity(), title, content, hashtag);
    }
}