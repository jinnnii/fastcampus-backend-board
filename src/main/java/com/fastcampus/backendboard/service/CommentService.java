package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.Comment;
import com.fastcampus.backendboard.domain.UserAccount;
import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import com.fastcampus.backendboard.repository.CommentRepository;
import com.fastcampus.backendboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> searchComments(Long articleId) {
        return commentRepository.findByArticle_Id(articleId).stream().map(CommentDto::from).toList();
    }

    public void saveComment(CommentDto dto){
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            commentRepository.save(dto.toEntity(article, userAccount));
        }catch (EntityNotFoundException e){
            log.warn("Failed Save Comment, Not Found Article or User - {}",e.getLocalizedMessage());
        }
    };
    public void updateComment(CommentDto dto){
        try {
            Comment comment = commentRepository.getReferenceById(dto.id());
            if(dto.content()!=null) comment.setContent(dto.content());
        }catch (EntityNotFoundException e){
            log.warn("Failed Update Comment, Not Found Article - dto :{}",dto);
        }
    };
    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
    };
}
