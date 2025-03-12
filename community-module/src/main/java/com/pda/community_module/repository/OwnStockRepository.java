package com.pda.community_module.repository;

import com.pda.community_module.domain.OwnStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OwnStockRepository extends JpaRepository<OwnStock, Long> {
    List<OwnStock> findByAccountId(Long accountId);

    // 기존 주식 정보 업데이트 (수량, 평균단가, 수익률)
    @Transactional
    @Modifying
    @Query("UPDATE OwnStock o SET o.stockCount = :stockCount, o.avgPrice = :avgPrice, o.profit = :profit WHERE o.id = :stockId")
    void updateStock(Long stockId, Long stockCount, Long avgPrice, Double profit);



}
