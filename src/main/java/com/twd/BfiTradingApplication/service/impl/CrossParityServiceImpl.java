package com.twd.BfiTradingApplication.service.impl;


import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import com.twd.BfiTradingApplication.service.CrossParityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CrossParityServiceImpl implements CrossParityService {

    @Autowired
    private CrossParityRepository crossParityRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public CrossParity createCrossParity(CrossParity crossParity) {
        return crossParityRepository.save(crossParity);
    }

    @Override
    public CrossParity updateCrossParity(Integer id, CrossParity crossParity) {
        Optional<CrossParity> optional = crossParityRepository.findById(id);
        if (optional.isPresent()) {
            CrossParity existing = optional.get();
            existing.setDescription(crossParity.getDescription());
            existing.setIdentifier(crossParity.getIdentifier());
            existing.setBaseCurrency(crossParity.getBaseCurrency());
            existing.setQuoteCurrency(crossParity.getQuoteCurrency());
            // Vous pouvez ajouter d'autres mises à jour en fonction de votre logique métier
            return crossParityRepository.save(existing);
        }
        return null; // ou lever une exception
    }

    @Override
    public CrossParity getCrossParityById(Integer id) {
        return crossParityRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCrossParity(Integer id) {
        crossParityRepository.deleteById(id);
    }

    @Override
    public List<CrossParity> getAllCrossParities() {
        return crossParityRepository.findAll();
    }

//    public void populateCrossParities() {
//        List<String[]> pairs = Arrays.asList(
//                new String[]{"EUR", "USD"},
//                new String[]{"GBP", "USD"},
//                new String[]{"USD", "JPY"},
//                new String[]{"AUD", "USD"},
//                new String[]{"USD", "CAD"},
//                new String[]{"USD", "CHF"},
//                new String[]{"NZD", "USD"},
//                new String[]{"EUR", "GBP"},
//                new String[]{"EUR", "JPY"},
//                new String[]{"GBP", "JPY"},
//                new String[]{"EUR", "CHF"},
//                new String[]{"EUR", "CAD"},
//                new String[]{"EUR", "AUD"},
//                new String[]{"EUR", "NZD"},
//                new String[]{"GBP", "CHF"},
//                new String[]{"GBP", "CAD"},
//                new String[]{"GBP", "AUD"},
//                new String[]{"GBP", "NZD"},
//                new String[]{"AUD", "JPY"},
//                new String[]{"AUD", "CHF"},
//                new String[]{"AUD", "CAD"},
//                new String[]{"AUD", "NZD"},
//                new String[]{"CAD", "JPY"},
//                new String[]{"CAD", "CHF"},
//                new String[]{"NZD", "JPY"},
//                new String[]{"NZD", "CHF"},
//                new String[]{"CHF", "JPY"},
//                new String[]{"USD", "MXN"},
//                new String[]{"USD", "SGD"},
//                new String[]{"USD", "HKD"},
//                new String[]{"USD", "SEK"},
//                new String[]{"USD", "NOK"},
//                new String[]{"USD", "DKK"},
//                new String[]{"USD", "ZAR"},
//                new String[]{"USD", "CNY"},
//                new String[]{"USD", "INR"},
//                new String[]{"EUR", "SGD"},
//                new String[]{"EUR", "HKD"},
//                new String[]{"EUR", "SEK"},
//                new String[]{"EUR", "NOK"},
//                new String[]{"EUR", "DKK"},
//                new String[]{"EUR", "ZAR"},
//                new String[]{"EUR", "CNY"},
//                new String[]{"GBP", "SGD"},
//                new String[]{"GBP", "HKD"},
//                new String[]{"GBP", "ZAR"},
//                new String[]{"AUD", "SGD"},
//                new String[]{"AUD", "HKD"},
//                new String[]{"CAD", "SGD"},
//                new String[]{"CAD", "HKD"},
//                new String[]{"NZD", "SGD"},
//                new String[]{"NZD", "HKD"}
//        );
//
//        pairs.forEach(pair -> {
//            String baseCode = pair[0];
//            String quoteCode = pair[1];
//
//            // Vérification et ajout des devises si nécessaire
//            Currency baseCurrency = currencyRepository.findByIdentifier(baseCode)
//                    .orElseGet(() -> currencyRepository.save(new Currency(baseCode, baseCode, 2)));
//
//            Currency quoteCurrency = currencyRepository.findByIdentifier(quoteCode)
//                    .orElseGet(() -> currencyRepository.save(new Currency(quoteCode, quoteCode, 2)));
//
//            // Création de CrossParity
//            String identifier = baseCode + "/" + quoteCode;
//            if (!crossParityRepository.existsByIdentifier(identifier)) {
//                CrossParity crossParity = new CrossParity(
//                        baseCode + " to " + quoteCode,
//                        identifier,
//                        baseCurrency,
//                        quoteCurrency,
//                        fetchedRate
//                );
//                crossParityRepository.save(crossParity);
//            }
//        });
//    }

    @Override
    public CrossParity updateFavorie(Integer id, boolean favorite) {
        Optional<CrossParity> optionalCrossParity = crossParityRepository.findById(id);

        if (optionalCrossParity.isPresent()) {
            CrossParity crossParity = optionalCrossParity.get();
            crossParity.setFavorite(favorite);
            return crossParityRepository.save(crossParity);
        } else {
            throw new EntityNotFoundException("CrossParity not found with ID: " + id);
        }
    }



    public List<String> getAllCrossParityIdentifiers() {
        return crossParityRepository.findAll().stream()
                .map(crossParity -> crossParity.getIdentifier())  // Get the identifier directly
                .collect(Collectors.toList());
    }


}
