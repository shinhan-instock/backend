package com.pda.community_module.repository;

import com.pda.community_module.domain.Account;
import com.pda.community_module.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId_UserId(String userId);
}
