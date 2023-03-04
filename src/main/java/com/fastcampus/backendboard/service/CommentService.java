package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import com.fastcampus.backendboard.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public List<CommentDto> searchComments(Long articleId) {
        return List.of();
    }
}
