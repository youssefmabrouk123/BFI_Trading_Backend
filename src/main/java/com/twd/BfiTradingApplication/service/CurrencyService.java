package com.twd.BfiTradingApplication.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public Currency createCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public Currency updateCurrency(Integer id, Currency currency) {
        Optional<Currency> existing = currencyRepository.findById(id);
        if (existing.isPresent()) {
            Currency current = existing.get();
            current.setDescription(currency.getDescription());
            current.setIdentifier(currency.getIdentifier());
            current.setNbrDec(currency.getNbrDec());
            return currencyRepository.save(current);
        }
        return null; // ou lever une exception
    }

    public Currency getCurrencyById(Integer id) {
        return currencyRepository.findById(id).orElse(null);
    }

    public void deleteCurrency(Integer id) {
        currencyRepository.deleteById(id);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public void saveCurrenciesFromCsv(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<Currency> csvToBean = new CsvToBeanBuilder<Currency>(reader)
                    .withType(Currency.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Currency> currencies = csvToBean.parse();
            currencyRepository.saveAll(currencies);
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors du traitement du fichier CSV : " + ex.getMessage());
        }
    }
}