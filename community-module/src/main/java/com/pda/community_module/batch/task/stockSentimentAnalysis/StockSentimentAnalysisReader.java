package com.pda.community_module.batch.task.stockSentimentAnalysis;

import com.pda.community_module.domain.Sentiment;
import com.pda.community_module.repository.SentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class StockSentimentAnalysisReader implements ItemReader<List<Sentiment>> {

    private final SentimentRepository sentimentRepository;
    private boolean hasRead = false;

    @Override
    public List<Sentiment> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (hasRead) {
            return null;
        }

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        hasRead = true;
        return sentimentRepository.findRecentSentiments(oneHourAgo);
    }
}
