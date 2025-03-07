package com.pda.community_module.web.controller;

import com.pda.community_module.service.CommentService;
import com.pda.community_module.web.dto.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponseDTO.getCommentDTO> createComment(
            @RequestBody CommentResponseDTO.createCommentDTO requestDTO) {
        log.info("댓글 생성 API 호출됨 - postId: {}, userId: {}", requestDTO.getPostId(), requestDTO.getUserId());
        return ResponseEntity.ok(commentService.createComment(requestDTO));
    }

    // 댓글 조회 여러개
    @GetMapping("/post/{postId}/infinite")
    public ResponseEntity<Page<CommentResponseDTO.getCommentDTO>> getCommentsByPostIdWithCursor(
            @PathVariable Long postId,
            @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        Page<CommentResponseDTO.getCommentDTO> comments = commentService.getCommentsByPostIdWithCursor(postId, lastCommentId, limit);
        return ResponseEntity.ok(comments);
    }


    // 특정 댓글 단건 조회
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO.getCommentDTO> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO.getCommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentResponseDTO.updateCommentDTO requestDTO) {
        return ResponseEntity.ok(commentService.updateComment(commentId, requestDTO));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @RequestParam("userId") Long requestUserId) {
        commentService.deleteComment(commentId, requestUserId);
        return ResponseEntity.noContent().build();
    }
}
