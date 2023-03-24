package com.fastcampus.backendboard.dto.request;

import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.dto.UserAccountDto;

public record CommentRequest(
        Long articleId,
        Long parentCommentId,
        String content
) {
    public static CommentRequest of (Long articleId, String content) {
        return CommentRequest.of(articleId, null, content);
    }

    public static CommentRequest of (Long articleId, Long parentCommentId, String content) {
        return new CommentRequest(articleId, parentCommentId, content);
    }

    public CommentDto toDto(UserAccountDto userAccountDto){
        return CommentDto.of(articleId, parentCommentId, content, userAccountDto);
    }
}
