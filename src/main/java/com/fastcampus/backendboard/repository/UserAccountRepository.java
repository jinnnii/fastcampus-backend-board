package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.domain.QUserAccount;
import com.fastcampus.backendboard.domain.UserAccount;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface UserAccountRepository extends
        JpaRepository<UserAccount,String> { }
