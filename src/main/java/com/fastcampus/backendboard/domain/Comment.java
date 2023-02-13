package com.fastcampus.backendboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdId")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter @ManyToOne(optional = false) private Article article;            //게시글
    @Setter @Column(nullable = false, length = 500) private String content;             //본문

    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt;        //생성일
    @CreatedBy @Column(nullable = false) private String createdId;               //생성자
    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt;       //수정일
    @LastModifiedBy @Column(nullable = false) private String modifiedId;              //수정자       // 수정자

    protected Comment() {}

    private Comment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public static Comment of(Article article, String content) {
        return new Comment(article, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return id!=null && id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
