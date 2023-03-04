package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.type.SearchType;
import com.fastcampus.backendboard.dto.ArticleDto;
import com.fastcampus.backendboard.dto.ArticleUpdateDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor //필수 필드 생성자
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType title, String searchKeyword) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(long id) {
        return null;
    }

    public void saveArticle(ArticleDto dto) {
        
    }

    public void updateArticle(long id, ArticleUpdateDto dto) {
    }

    public void deleteArticle(long id) {
    }
}
