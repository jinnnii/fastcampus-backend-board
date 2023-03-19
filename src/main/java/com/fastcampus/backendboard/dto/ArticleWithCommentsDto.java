package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Article;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsDto(
        LocalDateTime createdAt,
        String createdId,
        LocalDateTime modifiedAt,
        String modifiedId,
        Long id,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        UserAccountDto userAccountDto,
        Set<CommentDto> commentDtos

) {
    public static ArticleWithCommentsDto of(LocalDateTime createdAt,
                                  String createdId,
                                  LocalDateTime modifiedAt,
                                  String modifiedId,
                                  Long id,
                                  String title,
                                  String content,
                                  Set<HashtagDto> hashtagDtos,
                                  UserAccountDto userAccountDto,
                                  Set<CommentDto> commentDtos) {
        return new ArticleWithCommentsDto(createdAt, createdId, modifiedAt, modifiedId, id, title, content, hashtagDtos, userAccountDto, commentDtos);
    }

    public static ArticleWithCommentsDto from(Article entity){
        return new ArticleWithCommentsDto(
                entity.getCreatedAt(),
                entity.getCreatedId(),
                entity.getModifiedAt(),
                entity.getModifiedId(),
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getComments().stream()
                        .map(CommentDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
