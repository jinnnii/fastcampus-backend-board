package com.fastcampus.backendboard.repository.querydsl;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.QArticle;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {
    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        JPQLQuery<String> query = from(article).distinct().select(article.hashtag).where(article.hashtag.isNotNull());
        return query.fetch();
    }
}
