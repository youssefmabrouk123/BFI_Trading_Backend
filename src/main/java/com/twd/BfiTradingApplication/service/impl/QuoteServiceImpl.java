package com.twd.BfiTradingApplication.service.impl;
import com.twd.BfiTradingApplication.entity.Quote;
import com.twd.BfiTradingApplication.repository.QuoteRepository;
import com.twd.BfiTradingApplication.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    @Override
    public Quote createOrUpdateQuote(Quote quote) {
        return quoteRepository.save(quote);
    }

    @Override
    public Quote getQuoteById(Integer id) {
        return quoteRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteQuote(Integer id) {
        quoteRepository.deleteById(id);
    }

    @Override
    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }
}