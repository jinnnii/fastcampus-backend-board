package com.fastcampus.backendboard.dto;

import com.fastcampus.backendboard.domain.Hashtag;

import java.time.LocalDateTime;

public record HashtagDto(
        LocalDateTime createdAt,
        String createdId,
        LocalDateTime modifiedAt,
        String modifiedId,
        Long id,
        String hashtagName
) {
    public static HashtagDto of (LocalDateTime createdAt, String createdId, LocalDateTime modifiedAt, String modifiedId, Long id, String hashtagName) {
        return new HashtagDto(createdAt, createdId, modifiedAt, modifiedId, id, hashtagName);
    }

    public static HashtagDto of (String hashtagName){
        return new HashtagDto(null, null, null, null, null, hashtagName);
    }

    public static HashtagDto from(Hashtag entity){
        return HashtagDto.of(
                entity.getCreatedAt(),
                entity.getCreatedId(),
                entity.getModifiedAt(),
                entity.getModifiedId(),
                entity.getId(),
                entity.getHashtagName());
    }

    public Hashtag toEntity(){
        return Hashtag.of(hashtagName);
    }
}