package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long> {
}
