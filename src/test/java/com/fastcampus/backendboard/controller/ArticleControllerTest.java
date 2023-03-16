package com.fastcampus.backendboard.controller;

import com.fastcampus.backendboard.config.SecurityConfig;
import com.fastcampus.backendboard.config.TestSecurityConfig;
import com.fastcampus.backendboard.domain.constant.FormStatus;
import com.fastcampus.backendboard.domain.constant.SearchType;
import com.fastcampus.backendboard.dto.ArticleDto;
import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.dto.request.ArticleRequest;
import com.fastcampus.backendboard.dto.response.ArticleResponse;
import com.fastcampus.backendboard.service.ArticleService;
import com.fastcampus.backendboard.service.PaginationService;
import com.fastcampus.backendboard.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller test - Article")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;

    ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][GET] Article list page -200 OK")
    @Test
    void givenNothing_whenReqArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        given(articleService.searchArticles(eq(null),eq(null),any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));

        //When & Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        then(articleService).should().searchArticles(eq(null),eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    @DisplayName("[view][GET] Article list page -Paging, Sort")
    @Test
    void givenPagingAndSorting_whenReqArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1,2,3,4,5);

        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);
        //When
        mvc.perform(
                get("/articles")
                        .queryParam("page", String.valueOf(pageNumber))
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("sort", sortName+","+direction)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        //Then
        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] Article list page -Search")
    @Test
    void givenSearchTypeAndValue_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";

        given(articleService.searchArticles(eq(searchType), eq(searchKeyword), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));
        //When
        mvc.perform(
                get("/articles")
                        .queryParam("searchType", searchType.name())
                        .queryParam("searchKeyword", searchKeyword)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));

        //Then
        then(articleService).should().searchArticles(eq(searchType), eq(searchKeyword), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] Article (detail) page - No Authentication")
    @Test
    void givenNothing_whenReqArticleView_thenRedirectsToLoginPage() throws Exception {
        //Given
        long articleId = 1L;

        //When & Then
        mvc.perform(get("/articles/"+articleId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(articleService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view][GET] Article (detail) page -200 OK")
    @Test
    void givenNothing_whenReqArticleView_thenReturnsArticleView() throws Exception {
        //Given
        long articleId = 1L;
        long totalCount = 1L;

        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(totalCount);
        //When & Then
        mvc.perform(get("/articles/"+articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("totalCount", totalCount));

        then(articleService).should().getArticleWithComments(articleId);
    }
    @Disabled
    @DisplayName("[view][GET] Article search page -200 OK")
    @Test
    void givenNothing_whenReqArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search"));
    }

    @DisplayName("[view][GET] Article hashtag search page -200 OK")
    @Test
    void givenNothing_whenReqArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        //Given
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(articleService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));

        //When & Then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags",hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    @DisplayName("[view][GET] Article hashtag search page -200 OK, Insert Hashtag")
    @Test
    void givenHashtag_whenReqArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        //Given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(articleService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));

        //When & Then
        mvc.perform( get("/articles/search-hashtag")
                        .queryParam("searchKeyword", hashtag)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags",hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    @WithMockUser
    @DisplayName("[view][GET] Article Create page -200 OK")
    @Test
    void givenNothing_whenReqArticleCreateView_thenReturnsArticleCreateView() throws Exception {
        //Given

        //When & Then
        mvc.perform( get("/articles/form") )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithMockUser
    @DisplayName("[view][GET] Article Update page -200 OK")
    @Test
    void givenArticleId_whenReqArticleUpdateView_thenReturnsArticleUpdateView() throws Exception {
        //Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);

        //When & Then
        mvc.perform( get("/articles/"+articleId+"/form") )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)));

        then(articleService).should().getArticle(articleId);
    }

    @DisplayName("[view][GET] 게시글 수정 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/"+articleId+"/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Save Article - Redirect ")
    @Test
    void givenArticleInfo_whenSavingArticle_thenReturnsArticlesView() throws Exception {
        //Given
        ArticleRequest article = createArticleRequest();

        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        //When & Then
        mvc.perform( post("/articles/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(article))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));

        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][PUT] Update Article -Redirect")
    @Test
    void givenArticleInfo_whenUpdatingArticle_thenReturnsArticleDetailView() throws Exception {
        //Given
        long articleId = 1L;
        ArticleRequest articleRequest = createArticleRequest("new title", "new content", "spring");
        willDoNothing().given(articleService).updateArticle(eq(articleId), any(ArticleDto.class));

        //When & Then
        mvc.perform( put("/articles/"+articleId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(articleRequest))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/"+articleId))
                .andExpect(redirectedUrl("/articles/"+articleId));

        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }

    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][DELETE] Delete Article -200 OK")
    @Test
    void givenArticleId_whenDeletingArticle_thenReturnsArticlesView() throws Exception {
        //Given
        long articleId = 1L;
        String userId ="test";

        willDoNothing().given(articleService).deleteArticle(articleId, userId);

        //When & Then
        mvc.perform( delete("/articles/"+articleId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));

        then(articleService).should().deleteArticle(articleId, userId);
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto(){
        return ArticleWithCommentsDto.of(
                LocalDateTime.now(),"kej",  LocalDateTime.now(), "kej", 1L, "title","content", "java", createUserAccountDto(), Set.of()
        );
    }

    private ArticleDto createArticleDto(){
        return ArticleDto.of(createUserAccountDto(), "title", "content", "java");
    }

    private ArticleRequest createArticleRequest(){
        return ArticleRequest.of("title", "content", "java");
    }

    private ArticleRequest createArticleRequest(String title, String content, String hashtag){
        return ArticleRequest.of(title, content, hashtag);
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                 "kej", "1234", "kej@email.com", "K", "this is memo", LocalDateTime.now(), "kej", LocalDateTime.now(), "kej"
        );
    }

}
