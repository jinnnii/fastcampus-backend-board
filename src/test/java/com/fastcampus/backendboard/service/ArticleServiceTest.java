package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.Hashtag;
import com.fastcampus.backendboard.domain.UserAccount;
import com.fastcampus.backendboard.domain.constant.SearchType;
import com.fastcampus.backendboard.dto.ArticleDto;
import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import com.fastcampus.backendboard.dto.HashtagDto;
import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import com.fastcampus.backendboard.repository.HashtagRepository;
import com.fastcampus.backendboard.repository.UserAccountRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private HashtagRepository hashtagRepository;

    @Mock private HashtagService hashtagService;

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
        then(hashtagRepository).shouldHaveNoInteractions();
        then(articleRepository).shouldHaveNoInteractions();
    }


    @DisplayName("When Searching not exist hashtag name , Return Empty page")
    @Test
    void givenNoneExistHashtag_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        //Given
        String hashtagName = "not";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(), pageable, 0));

        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("When Searching hashtag name , Return Articles With Paging")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnsArticlesPage() {
        //Given
        String hashtagName = "java";
        Pageable pageable = Pageable.ofSize(20);
        Article expectedArticle = createArticle();
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(expectedArticle), pageable, 1));

        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(articles).isEqualTo(new PageImpl<>(List.of(ArticleDto.from(expectedArticle)), pageable, 1));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("When Selecting article hashtag , Return Unique Hashtag list")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        //Given
        Article article = createArticle();
        List<String> expectedHashtags = List.of("java", "spring", "boot");
        given(hashtagRepository.findAllHashtagNames()).willReturn(expectedHashtags);

        //When
        List<String> actualHashtags= sut.getHashtags();

        //Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(hashtagRepository).should().findAllHashtagNames();
    }

    @DisplayName("When Selecting articleId, Return ArticleWithComments")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        //When
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title",article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()));
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Selecting articleId but No comments, Return Exception")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenThrowsException() {
        //Given
        long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());
        //When
        Throwable t = catchThrowable(()-> sut.getArticleWithComments(articleId));
        //Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No Article - articleId :"+ articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Selecting article, Return Article")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        //When
        ArticleDto dto = sut.getArticle(articleId);

        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title",article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()));
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Selecting no exist article, Return Exception")
    @Test
    void givenNoneExistentArticleId_whenSearchingArticle_thenReturnsException() {
        //Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //When
        Throwable t = catchThrowable(()->sut.getArticle(articleId));

        //Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No Article - articleId :"+ articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Inserting article, Save Article (with hashtag)")
        @Test
        void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
            //Given
            ArticleDto dto = createArticleDto();
            Set<String> expectedHashtagNames = Set.of("java","spring");
            Set<Hashtag> expectedHashtags = new HashSet<>();
            expectedHashtags.add(createHashtag("java"));

            given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
            given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
            given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);
            given(articleRepository.save(any(Article.class))).willReturn(createArticle());

            //When
            sut.saveArticle(dto); //제목, 본문, 아이디, 닉네임, 해시태그

            //Then
            then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
            then(hashtagService).should().parseHashtagNames(dto.content());
            then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
            then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("When Inserting no exist Modified article, Write warning logs")
    @Test
    void givenNoneExistArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        //Given
        Long articleId = 1L;
        ArticleDto dto = createArticleDto("new title", "new content");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //When
        sut.updateArticle(articleId, dto); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("When Inserting Modified article, Update Article")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("new title", "new content");
        Set<String> expectedHashtagNames = Set.of("springboot");
        Set<Hashtag> expectedHashtags = new HashSet<>();

        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);

        //When
        sut.updateArticle(dto.id(), dto); //제목, 본문, 아이디, 닉네임, 해시태그

        //Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .extracting("hashtags", as(InstanceOfAssertFactories.COLLECTION))
                .hasSize(1)
                .extracting("hashtagName")
                .containsExactly("springboot");

        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
    }

    @DisplayName("When Inserting articleId, Delete Article")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        //Given
        Long articleId = 1L;
        String userId = "kej";
        given(articleRepository.getReferenceById(articleId)).willReturn(createArticle());
        willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId, userId);
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());

        //When
        sut.deleteArticle(1L, userId);

        //Then
        then(articleRepository).should().getReferenceById(articleId);
        then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId, userId);
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
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
        return createArticle(1L);
    }
    private Article createArticle(Long id){
        Article article= Article.of(createUserAccount(),"title","content");
        article.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));

        ReflectionTestUtils.setField(article, "id", id);
        return article;
    }

    private Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    private Hashtag createHashtag(long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);
        return hashtag;
    }

    private ArticleDto createArticleDto(){
        return createArticleDto("title", "content");
    }

    private ArticleDto createArticleDto(String title, String content){
        return ArticleDto.of(
                1L,createUserAccountDto(),title,content, Set.of(HashtagDto.of("java")),LocalDateTime.now(), "kej",LocalDateTime.now(), "kej"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "kej","1234","kej@email.com","K","this is memo",LocalDateTime.now(), "kej",LocalDateTime.now(),"kej"
        );
    }
}
