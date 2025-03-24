package com.twd.BfiTradingApplication.service;
import com.twd.BfiTradingApplication.entity.Quote;
import com.twd.BfiTradingApplication.entity.CrossParity;

import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private  CrossParityRepository crossParityRepository;


    public Quote createOrUpdateQuote(Quote quote) {
        return quoteRepository.save(quote);
    }

    public Quote getQuoteById(Integer id) {
        return quoteRepository.findById(id).orElse(null);
    }

    public void deleteQuote(Integer id) {
        quoteRepository.deleteById(id);
    }

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

}