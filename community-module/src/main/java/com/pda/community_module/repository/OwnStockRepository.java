package com.pda.community_module.repository;

import com.pda.community_module.domain.OwnStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnStockRepository extends JpaRepository<OwnStock, Long> {
    List<OwnStock> findByAccountId(Long accountId);
}
