package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.Quote;

import java.util.List;

public interface QuoteService {
    Quote createOrUpdateQuote(Quote quote);
    Quote getQuoteById(Integer id);
    void deleteQuote(Integer id);
    List<Quote> getAllQuotes();
}