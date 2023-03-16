package com.fastcampus.backendboard.controller;

import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.dto.request.CommentRequest;
import com.fastcampus.backendboard.dto.security.BoardPrincipal;
import com.fastcampus.backendboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    public String saveComment(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            CommentRequest commentRequest){

        commentService.saveComment(commentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/"+ commentRequest.articleId();
    }

    @DeleteMapping("/{commentId}")
    public String saveComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Long articleId){
        commentService.deleteComment(commentId, boardPrincipal.getUsername());
        return "redirect:/articles/"+ articleId;
    }
}
