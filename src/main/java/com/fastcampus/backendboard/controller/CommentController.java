package com.fastcampus.backendboard.controller;

import com.fastcampus.backendboard.dto.UserAccountDto;
import com.fastcampus.backendboard.dto.request.CommentRequest;
import com.fastcampus.backendboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    public String saveComment(CommentRequest commentRequest){
        UserAccountDto userDto = UserAccountDto.of("kej",
                "1234",
                "kej@email.com",
                "K",
                null,
                null,
                null,
                null,
                null); //TODO: 추후에 인증 정보를 넣어주어야함
        commentService.saveComment(commentRequest.toDto(userDto));

        return "redirect:/articles/"+ commentRequest.articleId();
    }

    @DeleteMapping("/{commentId}")
    public String saveComment(@PathVariable Long commentId, Long articleId){
        commentService.deleteComment(commentId);
        return "redirect:/articles/"+ articleId;
    }
}
