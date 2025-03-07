package com.pda.community_module.service;

import com.pda.community_module.web.dto.CommentResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface CommentService {

//    List<CommentResponseDTO.getCommentDTO> getCommentsByPostId(Long postId);

    CommentResponseDTO.getCommentDTO getCommentById(Long commentId);

    CommentResponseDTO.getCommentDTO createComment(CommentResponseDTO.createCommentDTO requestDTO);

    CommentResponseDTO.getCommentDTO updateComment(Long commentId, CommentResponseDTO.updateCommentDTO requestDTO);

    Page<CommentResponseDTO.getCommentDTO> getCommentsByPostIdWithCursor(Long postId, Long lastCommentId, int limit);

    void deleteComment(Long commentId);
}
