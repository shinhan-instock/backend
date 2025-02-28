package com.pda.community_module.repository;

import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByLikesDesc();

    List<Post> findAllByUserIdIn(List<Long> userIds);

    // 특정 유저가 스크랩한 모든 게시글 ID 조회
    List<Long> findAllPostIdsByUserId(Long userId);

    // 특정 유저가 팔로우한 모든 게시글 ID 조회
    List<Long> findAllFollowingIdsByUserId(Long userId);

    List<Post> findAllByUserId(Long id);

    List<Post> findAllByHashtag(String name);
}
