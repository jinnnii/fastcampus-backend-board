package com.fastcampus.backendboard.domain;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdId")
})
@Entity
public class Article extends AuditingField{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Column(nullable = false)                   private String title;
    @Setter @Column(nullable = false, length = 10000)   private String content;
    @Setter                                             private String hashtag;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article" , cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<Comment> comments = new LinkedHashSet<>();

    protected Article(){}

    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title,content,hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return this.getId() !=null && this.getId().equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
