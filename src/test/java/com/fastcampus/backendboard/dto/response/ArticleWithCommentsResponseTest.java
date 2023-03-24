package com.fastcampus.backendboard.dto.response;
import com.fastcampus.backendboard.dto.ArticleWithCommentsDto;
import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.dto.HashtagDto;
import com.fastcampus.backendboard.dto.UserAccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO - 댓글을 포함한 게시글 응답 테스트")
class ArticleWithCommentsResponseTest {

    @DisplayName("자식 댓글이 없는 게시글 + 댓글 dto를 api 응답으로 변환할 때, 댓글을 시간 내림차순 + ID 오름차순으로 정리한다.")
    @Test
    void givenArticleWithCommentsDtoWithoutChildComments_whenMapping_thenOrganizesCommentsWithCertainOrder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<CommentDto> articleCommentDtos = Set.of(
                createCommentDto(1L, null, now),
                createCommentDto(2L, null, now.plusDays(1L)),
                createCommentDto(3L, null, now.plusDays(3L)),
                createCommentDto(4L, null, now),
                createCommentDto(5L, null, now.plusDays(5L)),
                createCommentDto(6L, null, now.plusDays(4L)),
                createCommentDto(7L, null, now.plusDays(2L)),
                createCommentDto(8L, null, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        assertThat(actual.commentsResponse())
                .containsExactly(
                        createCommentResponse(8L, null, now.plusDays(7L)),
                        createCommentResponse(5L, null, now.plusDays(5L)),
                        createCommentResponse(6L, null, now.plusDays(4L)),
                        createCommentResponse(3L, null, now.plusDays(3L)),
                        createCommentResponse(7L, null, now.plusDays(2L)),
                        createCommentResponse(2L, null, now.plusDays(1L)),
                        createCommentResponse(1L, null, now),
                        createCommentResponse(4L, null, now)
                );
    }

    @DisplayName("게시글 + 댓글 dto를 api 응답으로 변환할 때, 댓글 부모 자식 관계를 각각의 규칙으로 정렬하여 정리한다.")
    @Test
    void givenArticleWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithCertainOrders() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<CommentDto> commentDtos = Set.of(
                createCommentDto(1L, null, now),
                createCommentDto(2L, 1L, now.plusDays(1L)),
                createCommentDto(3L, 1L, now.plusDays(3L)),
                createCommentDto(4L, 1L, now),
                createCommentDto(5L, null, now.plusDays(5L)),
                createCommentDto(6L, null, now.plusDays(4L)),
                createCommentDto(7L, 6L, now.plusDays(2L)),
                createCommentDto(8L, 6L, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(commentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        assertThat(actual.commentsResponse())
                .containsExactly(
                        createCommentResponse(5L, null, now.plusDays(5)),
                        createCommentResponse(6L, null, now.plusDays(4)),
                        createCommentResponse(1L, null, now)
                )
                .flatExtracting(CommentResponse::childComments)
                .containsExactly(
                        createCommentResponse(7L, 6L, now.plusDays(2L)),
                        createCommentResponse(8L, 6L, now.plusDays(7L)),
                        createCommentResponse(4L, 1L, now),
                        createCommentResponse(2L, 1L, now.plusDays(1L)),
                        createCommentResponse(3L, 1L, now.plusDays(3L))
                );
    }

    @DisplayName("게시글 + 댓글 dto를 api 응답으로 변환할 때, 부모 자식 관계 깊이(depth)는 제한이 없다.")
    @Test
    void givenArticleWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithoutDepthLimit() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<CommentDto> articleCommentDtos = Set.of(
                createCommentDto(1L, null, now),
                createCommentDto(2L, 1L, now.plusDays(1L)),
                createCommentDto(3L, 2L, now.plusDays(2L)),
                createCommentDto(4L, 3L, now.plusDays(3L)),
                createCommentDto(5L, 4L, now.plusDays(4L)),
                createCommentDto(6L, 5L, now.plusDays(5L)),
                createCommentDto(7L, 6L, now.plusDays(6L)),
                createCommentDto(8L, 7L, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        Iterator<CommentResponse> iterator = actual.commentsResponse().iterator();
        long i = 1L;
        while (iterator.hasNext()) {
            CommentResponse articleCommentResponse = iterator.next();
            assertThat(articleCommentResponse)
                    .hasFieldOrPropertyWithValue("id", i)
                    .hasFieldOrPropertyWithValue("parentCommentId", i == 1L ? null : i - 1L)
                    .hasFieldOrPropertyWithValue("createdAt", now.plusDays(i - 1L));

            iterator = articleCommentResponse.childComments().iterator();
            i++;
        }
    }


    private ArticleWithCommentsDto createArticleWithCommentsDto(Set<CommentDto> commentDtos) {
        return ArticleWithCommentsDto.of(
                LocalDateTime.now(),
                "kej",
                LocalDateTime.now(),
                "kej",
                1L,
                "title",
                "content",
                Set.of(HashtagDto.of("java")),
                createUserAccountDto(),
                commentDtos
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "kej",
                "1234",
                "kej@email.com",
                "K",
                "This is memo",
                LocalDateTime.now(),
                "kej",
                LocalDateTime.now(),
                "kej"
        );
    }

    private CommentDto createCommentDto(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return CommentDto.of(
                id,
                1L,
                parentCommentId,
                "content "+id,
                createdAt,
                "kej",
                createdAt,
                "kej",
                createUserAccountDto()
        );
    }

    private CommentResponse createCommentResponse(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return CommentResponse.of(
                id,
                parentCommentId,
                "content "+id,
                createdAt,
                "kej@email.com",
                "K",
                "kej"
        );
    }

}