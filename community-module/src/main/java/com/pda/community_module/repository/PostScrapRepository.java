package com.pda.community_module.repository;

import com.pda.community_module.domain.User;
import com.pda.community_module.domain.mapping.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    List<PostScrap> findByUser(User user);
}
