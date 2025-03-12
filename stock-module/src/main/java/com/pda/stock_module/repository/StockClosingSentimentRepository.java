package com.pda.stock_module.repository;

import com.pda.stock_module.domain.StockClosingSentiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StockClosingSentimentRepository extends JpaRepository<StockClosingSentiment, Long> {
    List<StockClosingSentiment> findByStockNameAndCreatedAtBetween(String stockName, LocalDateTime startDate, LocalDateTime endDate);
}
