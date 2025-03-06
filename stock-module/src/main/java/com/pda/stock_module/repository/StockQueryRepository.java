package com.pda.stock_module.repository;

import com.pda.stock_module.domain.common.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockQueryRepository extends JpaRepository<Company, String> {

    Company findByStockName(String stockName);
}
