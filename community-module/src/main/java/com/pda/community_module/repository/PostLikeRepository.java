package com.pda.community_module.repository;

import com.pda.community_module.domain.mapping.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
