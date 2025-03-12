package com.pda.community_module.repository;

import com.pda.community_module.domain.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


public interface SentimentRepository extends JpaRepository<Sentiment,Long> {

    @Query("SELECT s FROM Sentiment s WHERE s.createdAt >= :oneHourAgo")
    @Transactional
    List<Sentiment> findRecentSentiments(@Param("oneHourAgo") LocalDateTime oneHourAgo);
}
