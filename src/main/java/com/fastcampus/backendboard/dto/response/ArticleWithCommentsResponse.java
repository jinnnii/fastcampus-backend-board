package com.fastcampus.backendboard.dto.response;

import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Article} entity
 */
public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<CommentResponse> commentsResponse
){
    public static ArticleWithCommentsResponse of(Long id,
                                                 String title,
                                                 String content,
                                                 String hashtag,
                                                 LocalDateTime createdAt,
                                                 String email,
                                                 String nickname,
                                                 String userId,
                                                 Set<CommentResponse> commentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtag, createdAt, email, nickname, userId, commentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto){
        String nickname = dto.userAccountDto().nickname();
        if(nickname==null || nickname.isBlank()){
            nickname = dto.userAccountDto().userId();
        }
        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                dto.commentDtos().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}