package com.pda.community_module.repository;

import com.pda.community_module.domain.User;
import com.pda.community_module.domain.mapping.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    List<PostLike> findAllByPostId(Long id);
}
