package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.UserAccount;
import java.time.LocalDateTime;

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

    public static ArticleDto of(
                                UserAccountDto userAccountDto,
                                String title,
                                String content,
                                String hashtag) {
        return new ArticleDto(null, userAccountDto, title, content, hashtag, null, null, null, null);
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

    public Article toEntity(UserAccount userAccount){
        return Article.of(userAccount, title, content, hashtag);
    }
}
