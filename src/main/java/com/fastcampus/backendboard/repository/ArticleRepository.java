package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article,Long>,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle> {
    @Override // 검색에 대한 세부적인 구성
    default void customize(QuerydslBindings bindings, QArticle root)
    {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdId);
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase);        // like '${v}'
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);      // like '%${v}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdId).first(StringExpression::containsIgnoreCase);
    }
}
