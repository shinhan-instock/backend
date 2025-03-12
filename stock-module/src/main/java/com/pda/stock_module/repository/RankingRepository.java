package com.pda.stock_module.repository;
import com.pda.stock_module.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByStockCode(String stockCode);
    Optional<Ranking> findByStockName(String stockName);

//    @Query("SELECT r.currentPrice FROM Ranking r WHERE r.stockCode = :stockCode")
//    Optional<Long> findCurrentPriceByStockCode(@Param("stockCode") String stockCode);


    @Query("SELECT r FROM Ranking r WHERE r.fluctuationRank IS NOT NULL ORDER BY CAST(r.priceChangeRate AS double) DESC")
    List<Ranking> findTop20ByFluctuationRank();



}