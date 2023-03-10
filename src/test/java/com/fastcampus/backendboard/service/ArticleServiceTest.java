package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.UserAccount;
import com.fastcampus.backendboard.domain.type.SearchType;
import com.fastcampus.backendboard.dto.ArticleDto;
import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @DisplayName("When no Searching articles, Return Articles with Paging")
    @Test
    void givenNoSearchingParams_whenSearchingArticles_thenReturnsArticles(){
        //Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        //When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }
    @DisplayName("When Searching articles, Return Articles With Paging")
    @Test
    void givenSearchParams_whenSearchingArticles_thenReturnsArticles() {
        //Given
        SearchType searchType = SearchType.TITLE;
        String keyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(keyword, pageable)).willReturn(Page.empty());

        //When
        Page<ArticleDto> articles = sut.searchArticles(searchType, keyword, pageable); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(keyword,pageable);

    }

    @DisplayName("When Searching article hashtag (no search) , Return Empty page")
    @Test
    void givenNoSearchPrams_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);

        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(articles).isEmpty();
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("When Selecting article hashtag , Return Unique Hashtag list")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        //Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);

        //When
        List<String> actualHashtags= sut.getHashtags();

        //Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }

    @DisplayName("When Selecting article, Return Article")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        //When
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title",article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Selecting no exist article, Return Exception")
    @Test
    void givenNoneExistentArticleId_whenSearchingArticle_thenReturnsException() {
        //Given
        Long articleId = 0L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //When
        Throwable t = catchThrowable(()->sut.getArticle(articleId));

        //Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("No Article - articleId :"+ articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Inserting article, Save Article")
        @Test
        void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
            //Given
            ArticleDto dto = createArticleDto();
            given(articleRepository.save(any(Article.class))).willReturn(createArticle());

            //When
            sut.saveArticle(dto); //제목, 본문, 아이디, 닉네임, 해시태그

            //Then
            then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("When Inserting no exist Modified article, Write warning logs")
    @Test
    void givenNoneExistArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        //Given
        ArticleDto dto = createArticleDto("new title", "new content", "spring");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //When
        sut.updateArticle(dto); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("When Inserting Modified article, Update Article")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("new title", "new content", "spring");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //When
        sut.updateArticle(dto); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("When Inserting articleId, Delete Article")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        //Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        //When
        sut.deleteArticle(1L);

        //Then
        then(articleRepository).should().deleteById(articleId);
    }

    @DisplayName("When Selecting article Count, Return Article Count")
    @Test
    void givenNothing_whenCountingArticles_thenReturnArticlesCount() {
        //Given
        Long expected  = 1L;
        given(articleRepository.count()).willReturn(expected );

        //When
        long actual = sut.getArticleCount();

        //
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    private UserAccount createUserAccount(){
        return UserAccount.of("kej","1234","kej@email.com","K","this is memo");
    }

    private Article createArticle(){
        return Article.of(createUserAccount(),"title","content","java");
    }

    private ArticleDto createArticleDto(){
        return createArticleDto("title", "content", "java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag){
        return ArticleDto.of(
                1L,createUserAccountDto(),title,content,hashtag,LocalDateTime.now(), "kej",LocalDateTime.now(), "kej"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,"kej","1234","kej@email.com","K","this is memo",LocalDateTime.now(), "kej",LocalDateTime.now(),"kej"
        );
    }
}