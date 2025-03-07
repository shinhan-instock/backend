package com.pda.community_module.repository;

import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.PostCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCountRepository extends JpaRepository<PostCount, Long> {
    Optional<PostCount> findByPost(Post post);
}
