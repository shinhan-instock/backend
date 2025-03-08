package com.pda.community_module.repository;

import com.pda.community_module.domain.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUserIdAndPassword(@Param("userId") String userId, @Param("password") String password);
    Optional<User> findByUserId(@Param("userId") String userId);
    boolean existsByNickname(String nickname);
    List<User> findByNicknameStartingWith(String keyword);
    Optional<User> findByNickname(String nickname);
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT uf.following.id FROM UserFollows uf WHERE uf.follower.id = :id)")
    List<User> findByIdAndJoinUserFollows(@Param("id") Long id);

}
