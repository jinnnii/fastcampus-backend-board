package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.type.SearchType;
import com.fastcampus.backendboard.dto.ArticleDto;
import com.fastcampus.backendboard.dto.ArticleUpdateDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static com.fastcampus.backendboard.domain.QArticle.article;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * 검색
 * 각 게시글 페이지 이동
 * 페이지네이션
 * 홈버튼 ▶ 게시판 페이지로 리다이렉션
 * 정렬 기능
 */
@DisplayName("Business Logic - Articles")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("When Searching articles, Return Articles With Paging")
    @Test
    void givenSearchParams_whenSearchingArticles_thenReturnsArticles() {
        //Given

        //When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword"); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(articles).isNotNull();
    }

    @DisplayName("When Selecting article, Return Article")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        //Given

        //When
        ArticleDto article = sut.searchArticle(1L); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(article).isNotNull();
    }

    @DisplayName("When Inserting article, Save Article")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        //Given
        ArticleDto dto = ArticleDto.of(LocalDateTime.now(), "Kej", "title", "content", "java");
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //When
        sut.saveArticle(dto); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("When Inserting Modified article, Update Article")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        ArticleUpdateDto dto = ArticleUpdateDto.of("title", "Kej", "java");
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //When
        sut.updateArticle(1L, dto); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("When Inserting articleId, Delete Article")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        //Given
        ArticleUpdateDto dto = ArticleUpdateDto.of("title", "Kej", "java");
        willDoNothing().given(articleRepository).delete(any(Article.class));

        //When
        sut.deleteArticle(1L);

        //Then
        then(articleRepository).should().delete(any(Article.class));
    }
}