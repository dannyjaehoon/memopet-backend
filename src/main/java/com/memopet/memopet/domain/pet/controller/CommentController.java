package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.member.dto.DeactivateMemberRequestDto;
import com.memopet.memopet.domain.member.dto.DeactivateMemberResponseDto;
import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.CommentService;
import com.memopet.memopet.domain.pet.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    // 추억 댓글 다건 조회
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/memory-comments")
    public ResponseEntity<CommentsResponseDto> findMemoryComments(CommentsRequestDto commentsRequestDto) {

        CommentsResponseDto commentsResponseDto = commentService.findMemoryCommentsByPetId(commentsRequestDto);
        return new ResponseEntity<>(commentsResponseDto, HttpStatus.OK);
    }


    // 댓글 단건 삭제
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/comment")
    public ResponseEntity<CommentDeleteResponseDto> deleteMemoryComment(@RequestBody CommentDeleteRequestDto commentDeleteRequestDto) {

        CommentDeleteResponseDto commentDeleteResponseDto = commentService.deleteComment(commentDeleteRequestDto);
        return new ResponseEntity<>(commentDeleteResponseDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("/comment")
    public ResponseEntity<CommentPostResponseDto> postAComment(@RequestBody CommentPostRequestDto commentPostResponseDto ) {

        CommentPostResponseDto commentsResponseDto  = commentService.PostAComment(commentPostResponseDto);
        return new ResponseEntity<>(commentsResponseDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping("/comment")
    public ResponseEntity<WarmCommentUpdateResponseDto> updateComment(@RequestBody WarmCommentUpdateRequestDto warmCommentUpdateRequestDto ) {

        WarmCommentUpdateResponseDto warmCommentUpdateResponseDto  = commentService.updateWarmComment(warmCommentUpdateRequestDto);
        return new ResponseEntity<>(warmCommentUpdateResponseDto, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/comments")
    public ResponseEntity<PetAndMemoryCommentsResponseDto> findComments(PetAndMemoryCommentsRequestDto petAndMemoryCommentsRequestDto) {
        PetAndMemoryCommentsResponseDto petAndMemoryCommentsResponseDto = null;
        if(petAndMemoryCommentsRequestDto.getDepth() == 1) { // 댓글 조회
            petAndMemoryCommentsResponseDto = commentService.findComments(petAndMemoryCommentsRequestDto);
        } else { // 대댓글 조회
            petAndMemoryCommentsResponseDto = commentService.findReply(petAndMemoryCommentsRequestDto);
        }

        return new ResponseEntity<>(petAndMemoryCommentsResponseDto, HttpStatus.OK);
    }
}
