package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.QuoteHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface QuoteHistoryService {
    QuoteHistory createQuoteHistory(QuoteHistory quoteHistory);
    QuoteHistory getQuoteHistoryById(Integer pk, LocalDateTime quoteTime);
    void deleteQuoteHistory(Integer pk, LocalDateTime quoteTime);
    List<QuoteHistory> getAllQuoteHistories();
}