package com.fastcampus.backendboard.dto.response;

import com.fastcampus.backendboard.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public record CommentResponse(
        Long id,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<CommentResponse> childComments

) {
    public static CommentResponse of (Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        return CommentResponse.of(id, null, content, createdAt, email, nickname, userId);
    }

    public static CommentResponse of (Long id, Long parentCommentId, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        Comparator<CommentResponse> childCommentComparator = Comparator
                .comparing(CommentResponse::createdAt)
                .thenComparingLong(CommentResponse::id);

        return new CommentResponse(id, parentCommentId, content, createdAt, email, nickname, userId, new TreeSet<>(childCommentComparator));
    }

    public static CommentResponse from(CommentDto dto){
        String nickname = dto.userAccountDto().nickname();
        if(nickname == null || nickname.isBlank()){
            nickname = dto.userAccountDto().userId();
        }
        return CommentResponse.of(
                dto.id(),
                dto.parentCommentId(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId()
        );
    }

    public boolean hasParentComment(){
        return parentCommentId!=null;
    }
}
