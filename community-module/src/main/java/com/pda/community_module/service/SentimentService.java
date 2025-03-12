package com.pda.community_module.service;

import org.springframework.web.client.RestTemplate;

public interface SentimentService {
    Long analyzeSentiment(String content);
}
