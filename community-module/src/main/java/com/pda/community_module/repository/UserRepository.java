package com.pda.community_module.repository;

import com.pda.community_module.domain.User;
import com.pda.community_module.domain.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userid);

    Optional<User> findByNickname(String nickname);
}
