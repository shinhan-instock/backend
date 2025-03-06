package com.pda.stock_module.repository;


import com.pda.stock_module.domain.StockClosingPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockClosingPriceRepository extends JpaRepository<StockClosingPrice, Long> {
}
