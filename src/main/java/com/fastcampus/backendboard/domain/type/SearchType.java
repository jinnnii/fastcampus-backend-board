package com.fastcampus.backendboard.domain.type;

import lombok.Getter;

public enum SearchType {
    TITLE("제목"),
    CONTENT("내용"),
    ID("사용자ID"),
    NICKNAME("닉네임"),
    HASHTAG("해시태그");

    @Getter private final String description;

    SearchType(String description) {
        this.description = description;
    }
}
