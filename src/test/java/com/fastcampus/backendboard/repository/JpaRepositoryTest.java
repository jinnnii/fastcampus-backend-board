package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.config.JpaConfig;
import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.UserAccount;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("testdb")
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Jpa repository test")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;


    JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                      @Autowired CommentRepository commentRepository,
                      @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.userAccountRepository = userAccountRepository;
    }
    @DisplayName("select test")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        //Given

        //When
        List<Article> articles = articleRepository.findAll();
        //Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert test")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        //Given
        long preCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("kej", "pw","kej@email.com","nickname",null));
        //When
        articleRepository.save(Article.of(userAccount,"new title", "new content", "#spring"));
        //Then
        assertThat(articleRepository.count())
                .isEqualTo(preCount+1);
    }

    @DisplayName("update test")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updHashtag = "#springboot";
        article.setHashtag(updHashtag);
        //When
        Article updArticle = articleRepository.save(article);
        //Then
        assertThat(updArticle)
                .hasFieldOrPropertyWithValue("hashtag", updHashtag);
    }

    @DisplayName("delete test")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long preArticleCnt = articleRepository.count();
        long preCommentCnt = commentRepository.count();
        long delCommentCnt = article.getComments().size();
        //When
        articleRepository.delete(article);
        //Then
        assertThat(articleRepository.count())
                .isEqualTo(preArticleCnt-1);
        assertThat(commentRepository.count())
                .isEqualTo(preCommentCnt-delCommentCnt);
    }
}