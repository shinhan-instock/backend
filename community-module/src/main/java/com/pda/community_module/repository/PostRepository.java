package com.pda.community_module.repository;

import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByLikesDesc();

    List<Post> findAllByUserIdIn(List<Long> userIds);

    List<Post> findAllByUserId(Long id);

    List<Post> findAllByHashtag(String name);

    @Query("""
        SELECT p FROM Post p 
        JOIN p.postCount pc 
        WHERE p.createdAt BETWEEN :startTime AND :endTime 
        AND p.deleted = false
        ORDER BY pc.likeCount DESC
        LIMIT 10
    """)
    List<Post> findTop10LikedPosts(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);


    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllByOrderByCreatedAtDesc();
}
