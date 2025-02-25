package com.twd.BfiTradingApplication.service.impl;


import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.entity.QuoteHistoryId;
import com.twd.BfiTradingApplication.repository.QuoteHistoryRepository;
import com.twd.BfiTradingApplication.service.QuoteHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteHistoryServiceImpl implements QuoteHistoryService {

    @Autowired
    private QuoteHistoryRepository quoteHistoryRepository;

    @Override
    public QuoteHistory createQuoteHistory(QuoteHistory quoteHistory) {
        return quoteHistoryRepository.save(quoteHistory);
    }

    @Override
    public QuoteHistory getQuoteHistoryById(Integer pk, LocalDateTime quoteTime) {
        QuoteHistoryId quoteHistoryId = new QuoteHistoryId(pk, quoteTime);
        return quoteHistoryRepository.findById(quoteHistoryId).orElse(null);
    }

    @Override
    public void deleteQuoteHistory(Integer pk, LocalDateTime quoteTime) {
        QuoteHistoryId quoteHistoryId = new QuoteHistoryId(pk, quoteTime);
        quoteHistoryRepository.deleteById(quoteHistoryId);
    }

    @Override
    public List<QuoteHistory> getAllQuoteHistories() {
        return quoteHistoryRepository.findAll();
    }
}
