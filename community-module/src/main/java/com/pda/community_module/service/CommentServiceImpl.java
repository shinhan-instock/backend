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
            // 초기 요청: 커서가 없으므로 최신 데이터 조회
            commentPage = commentRepository.findByPost_IdOrderByIdDesc(postId, pageable);
        } else {
            // 커서가 있는 경우: lastCommentId보다 작은 값 조회 (내림차순)
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

        // 2. DTO + 조회된 Post, User로 Comment 엔티티 생성
        Comment comment = CommentConverter.createCommentRequestToEntity(requestDTO, post, user);
        // 3. DB 저장
        commentRepository.save(comment);
        // 4. 저장된 엔티티 -> DTO 변환 후 반환

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

        // 요청된 userId가 null이거나 댓글 작성자의 비즈니스 아이디와 다르면 수정 불가
        if (requestDTO.getUserId() == null || !comment.getUser().getUserId().equals(requestDTO.getUserId())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED); // 적절한 에러 코드 사용
        }

        // userId가 일치하면 댓글 내용만 업데이트
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
        // 소프트 딜리트 방식으로 삭제 처리
        comment.delete(requestUser);
        // 필요한 경우, 업데이트를 DB에 반영하기 위해 flush() 호출
        commentRepository.flush();
    }
}
