package com.pda.community_module.repository;

import com.pda.community_module.domain.mapping.UserFollows;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFollowsRepository extends JpaRepository<UserFollows, Long> {
    Optional<UserFollows> findByFollowerIdAndFollowingId(@Param("follower")Long followerId, @Param("following")Long followingId);
}
