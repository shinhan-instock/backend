package com.pda.community_module.service;

import com.pda.community_module.web.dto.CommentResponseDTO;
import java.util.List;

public interface CommentService {

    List<CommentResponseDTO.getCommentDTO> getCommentsByPostId(Long postId);

    CommentResponseDTO.getCommentDTO getCommentById(Long commentId);

    CommentResponseDTO.getCommentDTO createComment(CommentResponseDTO.createCommentDTO requestDTO);

    CommentResponseDTO.getCommentDTO updateComment(Long commentId, CommentResponseDTO.updateCommentDTO requestDTO);

    void deleteComment(Long commentId);
}
