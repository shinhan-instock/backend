package com.pda.stock_module.repository;

import com.pda.stock_module.domain.StockClosingSentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockClosingSentimentRepository extends JpaRepository<StockClosingSentiment, Long> {
}
