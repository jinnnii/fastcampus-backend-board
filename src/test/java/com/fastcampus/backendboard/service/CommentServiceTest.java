package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import com.fastcampus.backendboard.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("")
@ExtendWith(MockitoExtension.class)
@Transactional
class CommentServiceTest {
    @InjectMocks private CommentService sut;
    @Mock private ArticleRepository articleRepository;
    @Mock private CommentRepository commentRepository;

    @DisplayName("When Searching articlesId, Return Comments")
    @Test
    void givenArticleId_whenSearchingComments_thenReturnComments() {
        //Given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(
                Article.of("title","content","java")
        ));
        //When
        List<CommentDto> comments = sut.searchComments(articleId);

        //Then
        assertThat(comments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("When Inserting comment, Save Comment")
    @Test
    void givenCommentInfo_whenSavingComment_thenSavesComment() {
        //Given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(
                Article.of("title","content","java")
        ));
        //When
        List<CommentDto> comments = sut.searchComments(articleId);

        //Then
        assertThat(comments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }
}