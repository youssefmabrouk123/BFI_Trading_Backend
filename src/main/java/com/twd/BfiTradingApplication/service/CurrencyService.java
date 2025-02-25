package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.Currency;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CurrencyService {
    Currency createCurrency(Currency currency);
    Currency updateCurrency(Integer id, Currency currency);
    Currency getCurrencyById(Integer id);
    void deleteCurrency(Integer id);
    List<Currency> getAllCurrencies();
    void saveCurrenciesFromCsv(MultipartFile file) ;

    }