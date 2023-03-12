package com.fastcampus.backendboard.controller;

import com.fastcampus.backendboard.config.SecurityConfig;
import com.fastcampus.backendboard.dto.CommentDto;
import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.dto.request.CommentRequest;
import com.fastcampus.backendboard.service.CommentService;
import com.fastcampus.backendboard.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller - Comment")
@Import({SecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(CommentController.class)
class CommentControllerTest {
    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private CommentService commentService;

    public CommentControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][POST] Insert Comment - 200 OK ")
    @Test
    void givenCommentInfo_whenRequesting_thenSavesComment() throws Exception {
        //Given
        long articleId = 1L;
        CommentRequest request = CommentRequest.of(articleId, "content");

        willDoNothing().given(commentService).saveComment(any(CommentDto.class));
        //When & Then
        mvc.perform(post("/comments/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(request))
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/"+articleId))
                .andExpect(redirectedUrl("/articles/"+articleId));

        then(commentService).should().saveComment(any(CommentDto.class));
    }

    @DisplayName("[view][DELETE] Delete Comment - 200 OK ")
    @Test
    void givenCommentIdAndArticleId_whenRequesting_thenDeletesComment() throws Exception {
        //Given
        long articleId = 1L;
        long commentId = 1L;

        willDoNothing().given(commentService).deleteComment(commentId);

        //When & Then
        mvc.perform(delete("/comments/"+commentId)
                        .queryParam("articleId", String.valueOf(articleId))
                        .queryParam("commentId", String.valueOf(commentId))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/"+articleId))
                .andExpect(redirectedUrl("/articles/"+articleId));

        then(commentService).should().deleteComment(commentId);
    }
}