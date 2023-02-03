package com.fastcampus.backendboard.domain;

import java.time.LocalDateTime;

public class Article {
    Long id;                        //
    String title;                   //제목
    String content;                 //본문
    String hashtag;                 //해시태그

    LocalDateTime createdAt;        //생성일
    String createdId;               //생성자
    LocalDateTime modifiedAt;       //수정일
    String modifiedId;              //수정자
}
