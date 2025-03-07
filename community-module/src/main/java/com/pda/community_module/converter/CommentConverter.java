package com.pda.community_module.converter;

import com.pda.community_module.domain.Comment;
import com.pda.community_module.web.dto.CommentResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    /**
     * Comment 엔티티 -> getCommentDTO 변환
     */
    public static CommentResponseDTO.getCommentDTO toCommentEntity(Comment comment) {
        CommentResponseDTO.getCommentDTO dto = new CommentResponseDTO.getCommentDTO();
        dto.setId(comment.getId());
        // ManyToOne 매핑이므로 Post, User 객체에서 ID를 가져옴
        dto.setPostId(comment.getPost().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setContent(comment.getContent());
        return dto;
    }

    /**
     * Comment 엔티티 리스트 -> List<getCommentDTO> 변환
     */
    public static List<CommentResponseDTO.getCommentDTO> toCommentListEntity(List<Comment> comments) {
        return comments.stream()
                .map(CommentConverter::toCommentEntity)
                .collect(Collectors.toList());
    }

    /**
     * createCommentDTO + Post, User 엔티티 -> Comment 엔티티 생성
     */
    public static Comment createCommentRequestToEntity(CommentResponseDTO.createCommentDTO requestDTO,
                                                       com.pda.community_module.domain.Post post,
                                                       com.pda.community_module.domain.User user) {
        return Comment.builder()
                .post(post)
                .user(user)
                .content(requestDTO.getContent())
                .build();
    }
}
