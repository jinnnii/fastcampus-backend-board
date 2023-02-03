package com.fastcampus.backendboard.domain;

import java.time.LocalDateTime;

public class Comment {
    Long id;
    Article article;            //게시글
    String content;             //본문

    LocalDateTime createdAt;    //생성일
    String createdId;           //생성자
    LocalDateTime modifiedAt;   //수정일
    String modifiedId;          // 수정자
}
