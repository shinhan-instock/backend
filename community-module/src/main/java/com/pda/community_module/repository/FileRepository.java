package com.pda.community_module.repository;

import com.pda.community_module.domain.File;
import com.pda.community_module.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByUserId(Long userId);
    Optional<File> findByPostId(Long userId);
    File findByUser(User user);

}