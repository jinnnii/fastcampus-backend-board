package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.type.SearchType;
import com.fastcampus.backendboard.dto.ArticleDto;
import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor //필수 필드 생성자
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword==null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
        return
                switch (searchType){
                    case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
                    case CONTENT -> articleRepository.findByContentContaining(searchKeyword,pageable).map(ArticleDto::from);
                    case HASHTAG -> articleRepository.findByHashtag(searchKeyword, pageable).map(ArticleDto::from);
                    case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword,pageable).map(ArticleDto::from);
                    case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword,pageable).map(ArticleDto::from);
                };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(long articleId) {
        return articleRepository.findById(articleId).map(ArticleWithCommentsDto::from)
                .orElseThrow(()->new EntityNotFoundException("No Article - articleId :"+articleId));
    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.id());
            if(dto.title()!=null) article.setTitle(dto.title());
            if(dto.content()!=null) article.setContent(dto.content());
            article.setHashtag(dto.hashtag());
        }catch (EntityNotFoundException e){
            log.warn("Failed update article. Not found article - dto:{}", dto);
        }
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    public long getArticleCount(){
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if(hashtag==null || hashtag.isBlank()) return Page.empty(pageable);
        return articleRepository.findByHashtag(hashtag,pageable).map(ArticleDto::from);
    }
    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }

}
