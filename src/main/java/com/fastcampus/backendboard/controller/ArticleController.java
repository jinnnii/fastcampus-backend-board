package com.fastcampus.backendboard.controller;

import com.fastcampus.backendboard.domain.constant.FormStatus;
import com.fastcampus.backendboard.domain.constant.SearchType;
import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.dto.request.ArticleRequest;
import com.fastcampus.backendboard.dto.response.ArticleResponse;
import com.fastcampus.backendboard.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.backendboard.dto.security.BoardPrincipal;
import com.fastcampus.backendboard.service.ArticleService;
import com.fastcampus.backendboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * /articles
 * /articles/{article-id}
 * /articles/search
 * /articles/search-hashtag
 */
@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
            @RequestParam(required = false)SearchType searchType,
            @RequestParam(required = false)String searchKeyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map){

        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchKeyword, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable long articleId, ModelMap map){
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("comments", article.commentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchHashtag(@RequestParam(required = false) String searchKeyword,
                                @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                ModelMap map){
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchKeyword,pageable).map(ArticleResponse::from);
        List<String> hashtags = articleService.getHashtags();
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }

    @GetMapping({"/form", "/{articleId}/form"})
    public String articleForm(@PathVariable(required = false) Long articleId, ModelMap map){
        if (articleId != null){
            ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));
            map.addAttribute("article", article);
            map.addAttribute("formStatus", FormStatus.UPDATE);
        }
        else map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping("/form")
    public String saveArticle(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest){
        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));
        return "redirect:/articles";
    }

    @PutMapping({"/{articleId}"})
    public String updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest){
        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));
        return "redirect:/articles/"+articleId;

    }

    @DeleteMapping("/{articleId}")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal){
        articleService.deleteArticle(articleId, boardPrincipal.username());
        return "redirect:/articles";
    }



}
