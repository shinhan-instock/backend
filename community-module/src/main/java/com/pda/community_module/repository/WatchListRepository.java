package com.pda.community_module.repository;

import com.pda.community_module.domain.WatchList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    // 기존 메서드
    List<WatchList> findByUserId(Long userId);

    // ✅ 페이징 지원하는 메서드 추가
    Page<WatchList> findByUserId(Long userId, Pageable pageable);
}
