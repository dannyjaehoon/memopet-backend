package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.CommentService;
import com.memopet.memopet.global.common.dto.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    // 추억 댓글 다건 조회
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/memory-comments")
    public RestResult findMemoryComments(CommentsRequestDto commentsRequestDto) {

        CommentsResponseDto commentsResponseDto = commentService.findMemoryCommentsByPetId(commentsRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findMemoryCommentsResponse", commentsResponseDto);

        return new RestResult(dataMap);
    }


    // 댓글 단건 삭제
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/comment")
    public RestResult deleteMemoryComment(@RequestBody CommentDeleteRequestDto commentDeleteRequestDto) {
        CommentDeleteResponseDto commentDeleteResponseDto = commentService.deleteComment(commentDeleteRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("commentDeletionResponse", commentDeleteResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("/comment")
    public RestResult postAComment(@RequestBody CommentPostRequestDto commentPostResponseDto ) {

        CommentPostResponseDto commentsResponseDto  = commentService.PostAComment(commentPostResponseDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("postCommentResponse", commentsResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping("/comment")
    public RestResult updateComment(@RequestBody WarmCommentUpdateRequestDto warmCommentUpdateRequestDto ) {

        WarmCommentUpdateResponseDto warmCommentUpdateResponseDto  = commentService.updateWarmComment(warmCommentUpdateRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("warmCommentUpdateResponse", warmCommentUpdateResponseDto);

        return new RestResult(dataMap);
    }
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/comments")
    public RestResult findComments(PetAndMemoryCommentsRequestDto petAndMemoryCommentsRequestDto) {
        PetAndMemoryCommentsResponseDto petAndMemoryCommentsResponseDto = null;
        if(petAndMemoryCommentsRequestDto.getDepth() == 1) { // 댓글 조회
            petAndMemoryCommentsResponseDto = commentService.findComments(petAndMemoryCommentsRequestDto);
        } else { // 대댓글 조회
            petAndMemoryCommentsResponseDto = commentService.findReply(petAndMemoryCommentsRequestDto);
        }

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findComments", petAndMemoryCommentsResponseDto);

        return new RestResult(dataMap);
    }
}
