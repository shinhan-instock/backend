package com.pda.stock_module.repository;

import com.pda.stock_module.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findByStockCode(String stockCode);
}
