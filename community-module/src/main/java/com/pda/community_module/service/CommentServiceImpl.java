package com.pda.community_module.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.pda.community_module.converter.CommentConverter;
import com.pda.community_module.domain.Comment;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.PostCount;
import com.pda.community_module.domain.User;
import com.pda.community_module.repository.CommentRepository;
import com.pda.community_module.repository.PostRepository;
import com.pda.community_module.repository.PostCountRepository;
import com.pda.community_module.repository.UserRepository;
import com.pda.community_module.web.dto.CommentResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCountRepository postCountRepository;

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    @Override
    public Page<CommentResponseDTO.getCommentDTO> getCommentsByPostIdWithCursor(Long postId, Long lastCommentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Comment> commentPage;

        if (lastCommentId == null) {
            commentPage = commentRepository.findByPost_IdOrderByIdDesc(postId, pageable);
        } else {
            commentPage = commentRepository.findByPost_IdAndIdLessThanOrderByIdDesc(postId, lastCommentId, pageable);
        }

        return commentPage.map(CommentConverter::toCommentEntity);
    }
    /**
     * 특정 댓글 단건 조회
     */
    @Override
    public CommentResponseDTO.getCommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return CommentConverter.toCommentEntity(comment);
    }

    /**
     * 댓글 생성
     */
    @Override
    @Transactional
    public CommentResponseDTO.getCommentDTO createComment(CommentResponseDTO.createCommentDTO requestDTO) {

        Long postId = requestDTO.getPostId();
        log.debug("댓글 생성 요청 - postId: {}", postId);
        // 1. 전달받은 postId, userId로 Post, User 엔티티 조회
        Post post = postRepository.findById(requestDTO.getPostId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        User user = userRepository.findByUserId(requestDTO.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        log.debug("조회된 Post: {}", post);

        Comment comment = CommentConverter.createCommentRequestToEntity(requestDTO, post, user);
        commentRepository.save(comment);

    // 3. PostCount 엔티티 조회 후 commentCount 증가
            PostCount postCount = postCountRepository.findByPost(post)
            .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        postCount.incrementCommentCount();
        postCountRepository.save(postCount);

        commentRepository.flush();

        return CommentConverter.toCommentEntity(comment);
}

    /**
     * 댓글 수정
     */
    @Override
    @Transactional
    public CommentResponseDTO.getCommentDTO updateComment(Long commentId, CommentResponseDTO.updateCommentDTO requestDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        if (requestDTO.getUserId() == null || !comment.getUser().getUserId().equals(requestDTO.getUserId())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        comment.updateContent(requestDTO.getContent());
        return CommentConverter.toCommentEntity(comment);
    }

    /**
     * 댓글 삭제
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long requestUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        comment.delete(requestUser);
        commentRepository.flush();
    }
}
