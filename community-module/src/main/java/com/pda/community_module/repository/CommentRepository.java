package com.pda.community_module.repository;

import com.pda.community_module.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // post 필드의 id를 기준으로 댓글 조회 (ManyToOne 매핑)
    List<Comment> findAllByPost_Id(Long postId);
}
