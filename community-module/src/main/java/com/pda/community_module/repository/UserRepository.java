package com.pda.community_module.repository;

import com.pda.community_module.domain.User;
import com.pda.community_module.domain.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
