package com.fastcampus.backendboard.dto.response;

import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.dto.UserAccountDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.Article} entity
 */
public record ArticleWithCommentResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Set<CommentResponse> commentResponses
) implements Serializable {
    public static ArticleWithCommentResponse of(Long id,
                                      String title,
                                      String content,
                                      String hashtag,
                                      LocalDateTime createdAt,
                                      String email,
                                      String nickname,
                                      Set<CommentResponse> commentResponses) {
        return new ArticleWithCommentResponse(id, title, content, hashtag, createdAt, email, nickname, commentResponses);
    }

    public static ArticleWithCommentResponse from(ArticleWithCommentsDto dto){
        String nickname = dto.userAccountDto().nickname();
        if(nickname==null || nickname.isBlank()){
            nickname = dto.userAccountDto().userId();
        }
        return new ArticleWithCommentResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.commentDtos().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}