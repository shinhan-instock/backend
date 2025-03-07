package com.pda.community_module.web.controller;

import com.pda.community_module.service.CommentService;
import com.pda.community_module.web.dto.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponseDTO.getCommentDTO> createComment(
            @RequestBody CommentResponseDTO.createCommentDTO requestDTO) {
        return ResponseEntity.ok(commentService.createComment(requestDTO));
    }

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO.getCommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
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
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
