package com.pda.community_module.repository;

import com.pda.community_module.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 기존: List<Comment> findAllByPost_Id(Long postId);
    // 커서 방식: postId와 마지막 댓글 id 기준 조회. 만약 초기 요청이면 lastCommentId를 null로 처리합니다.
    Page<Comment> findByPost_IdAndIdLessThanOrderByIdDesc(Long postId, Long lastCommentId, Pageable pageable);

    // 초기 요청용 (커서가 없는 경우 최신 데이터 조회)
    Page<Comment> findByPost_IdOrderByIdDesc(Long postId, Pageable pageable);
}
