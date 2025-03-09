package com.pda.community_module.repository;

import com.pda.community_module.domain.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentimentRepository extends JpaRepository<Sentiment,Long> {
}
