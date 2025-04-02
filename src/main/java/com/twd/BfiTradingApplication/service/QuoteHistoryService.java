package com.twd.BfiTradingApplication.service;


import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.entity.QuoteHistoryId;
import com.twd.BfiTradingApplication.repository.QuoteHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class  QuoteHistoryService {

    @Autowired
    private QuoteHistoryRepository quoteHistoryRepository;

    public QuoteHistory createQuoteHistory(QuoteHistory quoteHistory) {
        return quoteHistoryRepository.save(quoteHistory);
    }

    public QuoteHistory getQuoteHistoryById(Integer pk, LocalDateTime quoteTime) {
        QuoteHistoryId quoteHistoryId = new QuoteHistoryId(pk, quoteTime);
        return quoteHistoryRepository.findById(quoteHistoryId).orElse(null);
    }

    public void deleteQuoteHistory(Integer pk, LocalDateTime quoteTime) {
        QuoteHistoryId quoteHistoryId = new QuoteHistoryId(pk, quoteTime);
        quoteHistoryRepository.deleteById(quoteHistoryId);
    }

    public List<QuoteHistory> getAllQuoteHistories() {
        return quoteHistoryRepository.findAll();
    }



}
