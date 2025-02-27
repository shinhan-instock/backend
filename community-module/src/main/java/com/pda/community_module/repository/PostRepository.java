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
    @Query("SELECT ps.post.id FROM PostScrap ps WHERE ps.user.id = :userId")
    List<Long> findAllPostIdsByUserId(@Param("userId") Long userId);

    // 특정 유저가 팔로우한 모든 게시글 ID 조회
    @Query("SELECT uf.following.id FROM UserFollows uf WHERE uf.follower.id =:userId")
    List<Long> findAllFollowingIdsByUserId(@Param("userId") Long userId);


}
