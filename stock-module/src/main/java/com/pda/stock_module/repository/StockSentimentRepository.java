package com.pda.stock_module.repository;

import com.pda.stock_module.domain.StockClosingPrice;
import com.pda.stock_module.domain.StockSentiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface StockSentimentRepository extends JpaRepository<StockSentiment, Long> {
    Optional<StockSentiment> findByStockCode(String stockCode);
}
