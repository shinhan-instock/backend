package com.pda.stock_module.repository;


import com.pda.stock_module.domain.StockClosingPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockClosingPriceRepository extends JpaRepository<StockClosingPrice, Long> {
    List<StockClosingPrice>  findByStockNameAndCreatedAtBetween(String stockName, LocalDateTime today, LocalDateTime twoMonthsAgo);
}
