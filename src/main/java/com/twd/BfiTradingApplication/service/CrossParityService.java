package com.twd.BfiTradingApplication.service;


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
public class CrossParityService {

    @Autowired
    private CrossParityRepository crossParityRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    public CrossParity createCrossParity(CrossParity crossParity) {
        return crossParityRepository.save(crossParity);
    }

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

    public CrossParity getCrossParityById(Integer id) {
        return crossParityRepository.findById(id).orElse(null);
    }

    public void deleteCrossParity(Integer id) {
        crossParityRepository.deleteById(id);
    }

    public List<CrossParity> getAllCrossParities() {
        return crossParityRepository.findAll();
    }

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



    public List<CrossParity> findAll() {
        return crossParityRepository.findAll();
    }


    public Optional<CrossParity> findById(Integer id) {
        return crossParityRepository.findById(id);
    }

    public CrossParity save(CrossParity crossParity) {
        return crossParityRepository.save(crossParity);
    }

    public void deleteById(Integer id) {
        crossParityRepository.deleteById(id);
    }



}
