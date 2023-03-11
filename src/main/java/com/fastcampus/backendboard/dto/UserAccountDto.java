package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.UserAccount;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.fastcampus.backendboard.domain.UserAccount} entity
 */
public record UserAccountDto(
        String userId,
        String userPw,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdId,
        LocalDateTime modifiedAt,
        String modifiedId
){
    public static UserAccountDto of (
                          String userId,
                          String userPw,
                          String email,
                          String nickname,
                          String memo,
                          LocalDateTime createdAt,
                          String createdId,
                          LocalDateTime modifiedAt,
                          String modifiedId) {
        return new UserAccountDto(userId, userPw, email, nickname, memo, createdAt, createdId, modifiedAt, modifiedId);
    }

    public static UserAccountDto from(UserAccount entity){
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPw(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedId(),
                entity.getModifiedAt(),
                entity.getModifiedId()
        );
    }

    public UserAccount toEntity(){
        return UserAccount.of(userId, userPw, email, nickname, memo);
    }
}