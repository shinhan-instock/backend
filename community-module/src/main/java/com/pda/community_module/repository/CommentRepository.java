package com.pda.community_module.repository;

import com.pda.community_module.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost_IdAndIdLessThanOrderByIdDesc(Long postId, Long lastCommentId, Pageable pageable);

    Page<Comment> findByPost_IdOrderByIdDesc(Long postId, Pageable pageable);
}
