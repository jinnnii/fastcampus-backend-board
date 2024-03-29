package com.fastcampus.backendboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdId"),
        @Index(columnList = "createdAt")
})
@Entity
public class UserAccount extends AuditingField {
    @Id
    @Column(length = 50)
    private String userId;
    @Setter @Column(nullable = false)   private String userPw;
    @Setter @Column(length = 100)       private String email;
    @Setter @Column(length = 100)       private String nickname;
    @Setter                             private String memo;


    protected UserAccount(){}
    private UserAccount(String userId, String userPw, String email, String nickname, String memo, String createdId) {
        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
        this.createdId = createdId;
        this.modifiedId = createdId;
    }

    public static UserAccount of(String userId, String userPw, String email, String nickname, String memo){
        return UserAccount.of(userId, userPw, email, nickname, memo, null);
    }

    public static UserAccount of(String userId, String userPw, String email, String nickname, String memo, String createdId){
        return new UserAccount(userId, userPw, email, nickname, memo, createdId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getUserId()!=null && this.getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }
}
