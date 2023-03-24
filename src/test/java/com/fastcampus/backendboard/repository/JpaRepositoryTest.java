package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.Comment;
import com.fastcampus.backendboard.domain.Hashtag;
import com.fastcampus.backendboard.domain.UserAccount;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Jpa repository test")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                      @Autowired CommentRepository commentRepository,
                      @Autowired UserAccountRepository userAccountRepository,
                      @Autowired HashtagRepository hashtagRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
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
        Article article = Article.of(userAccount, "new title", "new content");
        article.addHashtag(Hashtag.of("spring"));

        //When
        articleRepository.save(article);
        //Then
        assertThat(articleRepository.count())
                .isEqualTo(preCount+1);
    }

    @DisplayName("update test")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();
        Hashtag updHashtag = Hashtag.of("spring");
        article.clearHashtags();
        article.addHashtags(Set.of(updHashtag));

        //When
        Article updArticle = articleRepository.save(article);
        //Then
        assertThat(updArticle.getHashtags())
                .hasSize(1)
                .extracting("hashtagName", String.class)
                .containsExactly(updHashtag.getHashtagName());
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

    @DisplayName("[Querydsl] 전체 hashtag 리스트에서 이름만 조회하기")
    @Test
    void givenNothing_whenQueryingHashtags_thenReturnsHashtagNames() {
        // Given

        // When
        List<String> hashtagNames = hashtagRepository.findAllHashtagNames();

        // Then
        assertThat(hashtagNames).hasSize(20);
    }

    @DisplayName("[Querydsl] hashtag 로 페이징된 게시글 검색하기")
    @Test
    void givenHashtagNamesAndPageable_whenQueryingArticles_thenReturnsArticlePage() {
        // Given
        List<String> hashtagNames = List.of("Goldenrod", "Mauv", "Red");
        Pageable pageable = PageRequest.of(0, 5, Sort.by(
                Sort.Order.desc("hashtags.hashtagName"),
                Sort.Order.asc("title")
        ));

        // When
        Page<Article> articlePage = articleRepository.findByHashtagNames(hashtagNames, pageable);

        // Then
        assertThat(articlePage.getContent()).hasSize(pageable.getPageSize());
        assertThat(articlePage.getContent().get(0).getTitle()).isEqualTo("Morbi porttitor lorem id ligula.");
        assertThat(articlePage.getContent().get(0).getHashtags())
                .extracting("hashtagName", String.class)
                .containsExactly("Red");
        assertThat(articlePage.getTotalElements()).isEqualTo(6);
        assertThat(articlePage.getTotalPages()).isEqualTo(2);
    }


    @DisplayName("Child comment select Test")
    @Test
    void givenParentCommentId_whenSelecting_thenReturnsChildComments() {
        //Given

        //When
        Optional<Comment> parentComment = commentRepository.findById(1L);

        //Then
        assertThat(parentComment).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                    .hasSize(10);
    }

    @DisplayName("Child comment select Test")
    @Test
    void givenParentComment_whenSaving_thenInsertsChildComment() {
        //Given
        Comment parentComment = commentRepository.getReferenceById(1L);
        Comment childComment = Comment.of(
                parentComment.getUserAccount(),
                parentComment.getArticle(),
                "co-comment"
        );

        //When
        parentComment.addChildComment(childComment);
        commentRepository.flush();

        //Then
        assertThat(commentRepository.findById(1L)).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(11);
    }

    @DisplayName("Child comment delete Test")
    @Test
    void givenCommentIdHavingChildComments_whenDeletingParentComment_thenDeletesEveryComments() {
        //Given
        Comment parentComment = commentRepository.getReferenceById(1L);
        long prevArticleCommentCount = commentRepository.count();

        //When
        commentRepository.delete(parentComment);

        //Then
        assertThat(commentRepository.count()).isEqualTo(prevArticleCommentCount-11);
    }

    @DisplayName("Child comment delete Test (cmtId, userId) ")
    @Test
    void givenCommentIdHavingChildCommentsAndUserId_whenDeletingParentComment_thenDeletesEveryComments() {
        //Given
        long prevArticleCommentCount = commentRepository.count();

        //When
        commentRepository.deleteByIdAndUserAccount_UserId(1L, "kej");

        //Then
        assertThat(commentRepository.count()).isEqualTo(prevArticleCommentCount-11);
    }



    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig{
        @Bean
        public AuditorAware<String> auditorAware(){
            return ()-> Optional.of("kej");
        }
    }
}
