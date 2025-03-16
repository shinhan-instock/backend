package com.pda.community_module.repository;

import com.pda.community_module.domain.User;
import com.pda.community_module.domain.WatchList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    List<WatchList> findByUserId(Long userId);

    Page<WatchList> findByUserId(Long userId, Pageable pageable);

    Optional<WatchList> findByUserAndStockName(User user, String stockName);
    boolean existsByUser_UserIdAndStockCode(String userId, String stockCode);

}
