package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.UserAccount;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdId,
        LocalDateTime modifiedAt,
        String modifiedId

) {
    public static ArticleDto of(Long id,
                                UserAccountDto userAccountDto,
                                String title,
                                String content,
                                Set<HashtagDto> hashtagDtos,
                                LocalDateTime createdAt,
                                String createdId,
                                LocalDateTime modifiedAt,
                                String modifiedId) {
        return new ArticleDto(id,
                userAccountDto,
                title,
                content,
                hashtagDtos,
                createdAt,
                createdId,
                modifiedAt,
                modifiedId);
    }

    public static ArticleDto of(
                                UserAccountDto userAccountDto,
                                String title,
                                String content,
                                Set<HashtagDto> hashtagDtos) {
        return new ArticleDto(null, userAccountDto, title, content, hashtagDtos, null, null, null, null);
    }
    public static ArticleDto from(Article entity)
    {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getCreatedId(),
                entity.getModifiedAt(),
                entity.getModifiedId()
        );
    }

    public Article toEntity(){
        return Article.of(userAccountDto.toEntity(), title, content);
    }

    public Article toEntity(UserAccount userAccount){
        return Article.of(userAccount, title, content);
    }
}
