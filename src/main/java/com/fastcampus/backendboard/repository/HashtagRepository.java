package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.domain.Hashtag;
import com.fastcampus.backendboard.domain.QHashtag;
import com.fastcampus.backendboard.repository.querydsl.HashtagRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource
public interface HashtagRepository extends
        JpaRepository<Hashtag,Long>,
        HashtagRepositoryCustom,
        QuerydslPredicateExecutor<Hashtag>,
        QuerydslBinderCustomizer<QHashtag> {

    Optional<Hashtag> findByHashtagName(String hashtagName);
    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);

    @Override
    default void customize(QuerydslBindings bindings, QHashtag root){
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.hashtagName, root.createdAt, root.createdId);
        bindings.bind(root.hashtagName).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdId).first(StringExpression::containsIgnoreCase);
    };
}
