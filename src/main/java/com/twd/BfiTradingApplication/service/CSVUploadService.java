package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

@Service
public class CSVUploadService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CrossParityRepository crossParityRepository;

    @Value("${csv.currencyFilePath}")
    private String currencyFilePath;

    @Value("${csv.crossParityFilePath}")
    private String crossParityFilePath;


    public void uploadCurrencyDetails() {
        try (BufferedReader br = new BufferedReader(new FileReader(currencyFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length >= 3) {
                    String identifier = columns[0].trim();
                    String description = columns[1].trim();
                    int nbrDec = Integer.parseInt(columns[2].trim());

                    Optional<Currency> existingCurrency = currencyRepository.findByIdentifier(identifier);
                    if (existingCurrency.isEmpty()) {
                        Currency currency = new Currency(description, identifier, nbrDec);
                        currencyRepository.save(currency);
                        System.out.println("Saved new Currency: " + identifier);
                    } else {
                        Currency currency = existingCurrency.get();
                        currency.setDescription(description);
                        currency.setNbrDec(nbrDec);
                        currencyRepository.save(currency);
                        System.out.println("Updated existing Currency: " + identifier);
                    }
                } else {
                    System.err.println("Invalid currency line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading currency CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Upload all CrossParity details from CSV.
     * Expected CSV format per line: description, identifier, rate
     * The identifier must be in the format "BASE/QUOTE", e.g., "USD/EUR".
     */
    public void uploadCrossParityDetails() {
        try (BufferedReader br = new BufferedReader(new FileReader(crossParityFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length >= 3) {
                    String description = columns[0].trim();
                    String identifier = columns[1].trim();
                    Double rate = Double.parseDouble(columns[2].trim());

                    String[] parts = identifier.split("/");
                    if (parts.length != 2) {
                        System.err.println("Invalid cross-parity identifier format: " + identifier);
                        continue;
                    }
                    String baseCode = parts[0].trim();
                    String quoteCode = parts[1].trim();

                    Optional<Currency> baseCurrencyOpt = currencyRepository.findByIdentifier(baseCode);
                    Optional<Currency> quoteCurrencyOpt = currencyRepository.findByIdentifier(quoteCode);

                    if (baseCurrencyOpt.isPresent() && quoteCurrencyOpt.isPresent()) {
                        if (!crossParityRepository.existsByIdentifier(identifier)) {
                            CrossParity crossParity = new CrossParity(
                                    description,
                                    identifier,
                                    baseCurrencyOpt.get(),
                                    quoteCurrencyOpt.get(),
                                    rate
                            );
                            crossParityRepository.save(crossParity);
                            System.out.println("Saved new CrossParity: " + identifier);
                        } else {
                            System.out.println("CrossParity already exists: " + identifier);
                        }
                    } else {
                        System.err.println("Missing currency for cross-parity " + identifier + " - base: " + baseCode + ", quote: " + quoteCode);
                    }
                } else {
                    System.err.println("Invalid cross-parity line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cross-parity CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
