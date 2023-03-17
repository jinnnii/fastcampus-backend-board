package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.domain.Article;
import com.fastcampus.backendboard.domain.Comment;
import com.fastcampus.backendboard.domain.UserAccount;
import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.repository.ArticleRepository;
import com.fastcampus.backendboard.repository.CommentRepository;
import com.fastcampus.backendboard.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("")
@ExtendWith(MockitoExtension.class)
@Transactional
class CommentServiceTest {
    @InjectMocks private CommentService sut;
    @Mock private ArticleRepository articleRepository;
    @Mock private CommentRepository commentRepository;

    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("When Searching articlesId, Return Comments")
    @Test
    void givenArticleId_whenSearchingComments_thenReturnComments() {
        //Given
        Long articleId = 1L;
        Comment expected = createComment("content");
        given(commentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));
        //When
        List<CommentDto> actual = sut.searchComments(articleId);

        //Then
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(commentRepository).should().findByArticle_Id(articleId);
    }

    @DisplayName("When Inserting comment, Save Comment")
    @Test
    void givenCommentInfo_whenSavingComment_thenSavesComment() {
        //Given
        CommentDto dto = createCommentDto("comment");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(commentRepository.save(any(Comment.class))).willReturn(null);

        //When
        sut.saveComment(dto);

        //Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(commentRepository).should().save(any(Comment.class));
    }

    @DisplayName("When Inserting comment on a Article not exist, Write warning logs.")
    @Test
    void givenNoneExistArticle_whenSavingComment_thenLogsSituationAndDoesNothing(){
        ///Given
        CommentDto dto = createCommentDto("comment");
        given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        //When
        sut.saveComment(dto);

        //Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(commentRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("When Inserting comment, Update comment")
    @Test
    void givenCommentInfo_whenUpdatingComment_thenUpdatesComment(){
        //Given
        String oldContent = "content";
        String newContent = "new content";
        Comment comment = createComment(oldContent);
        CommentDto dto = createCommentDto(newContent);
        given(commentRepository.getReferenceById(dto.id())).willReturn(comment);

        //When
        sut.updateComment(dto);

        //Then
        assertThat(comment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(newContent);
        then(commentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("When Updating not exist comment, Write warning logs.")
    @Test
    void givenNoneExistComment_whenUpdatingComment_thenLogsSituationAndDoesNothing(){
        ///Given
        CommentDto dto = createCommentDto("comment");
        given(commentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //When
        sut.updateComment(dto);

        //Then
        then(commentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("When Deleting comment, Deletes comment.")
    @Test
    void givenCommentId_whenDeletingComment_thenDeletesComment(){
        ///Given
        Long commentId = 1L;
        String userId = "kej";
        willDoNothing().given(commentRepository).deleteByIdAndUserAccount_UserId(commentId, userId);

        //When
        sut.deleteComment(commentId, userId);

        //Then
        then(commentRepository).should().deleteByIdAndUserAccount_UserId(commentId, userId);
    }

    private CommentDto createCommentDto(String content){
        return CommentDto.of(
                1L,1L, content, LocalDateTime.now(),"kej",LocalDateTime.now(),"kej",createUserAccountDto()
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "kej","1234","kej@email.com","K","This is memo", LocalDateTime.now(),"kej",LocalDateTime.now(),"kej"
        );
    }

    private Comment createComment(String content){
        return Comment.of(createUserAccount(),
                Article.of(createUserAccount(), "new title", "new content", "java"),
                content
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "kej","1234","kej@email.com","K","this is memo"
        );
    }

    private Article createArticle(){
        return Article.of(
                createUserAccount(),"title","content","spring"
        );
    }

}
