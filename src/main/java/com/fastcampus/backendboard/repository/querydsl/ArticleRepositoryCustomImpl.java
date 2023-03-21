package com.fastcampus.backendboard.repository.querydsl;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.QArticle;
import com.fastcampus.backendboard.domain.QHashtag;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collection;
import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {
    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        return from(article)
                .distinct()
                .select(article.hashtags.any().hashtagName)
                .fetch();
    }
    @Override
    public Page<Article> findByHashtagNames(Collection<String> names, Pageable pageable) {
        QArticle article = QArticle.article;
        QHashtag hashtag = QHashtag.hashtag;

        JPQLQuery<Article> query = from(article)
                .innerJoin(article.hashtags, hashtag)
                .where(hashtag.hashtagName.in(names));

        List<Article> articles = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(articles, pageable, query.fetchCount());
    }
}
