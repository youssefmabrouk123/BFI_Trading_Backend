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
import java.math.BigDecimal;
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
                if (columns.length >= 4) {
                    String description = columns[0].trim();
                    String identifier = columns[1].trim();

                    double rate;
                    BigDecimal quotity;
                    try {
                        rate = Double.parseDouble(columns[2].trim());
                        quotity = new BigDecimal(columns[3].trim());

                        if (quotity.compareTo(BigDecimal.ZERO) <= 0) {
                            System.err.println("Invalid quotity value (must be > 0) for: " + identifier);
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in line: " + line);
                        continue;
                    }

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
                        Currency baseCurrency = baseCurrencyOpt.get();
                        Currency quoteCurrency = quoteCurrencyOpt.get();

                        // Adjust rate based on quotity
                      //  double adjustedRate = rate / quotity.doubleValue();

                        if (!crossParityRepository.existsByIdentifier(identifier)) {
                            CrossParity crossParity = new CrossParity(
                                    description,
                                    identifier,
                                    baseCurrency,
                                    quoteCurrency,
                                    rate,
                                    quotity
                            );
                            crossParityRepository.save(crossParity);
                            System.out.println("Saved new CrossParity: " + identifier + " with  rate: " + rate);
                        } else {
                            System.out.println("CrossParity already exists: " + identifier);
                        }
                    } else {
                        System.err.println("Missing currency for cross-parity " + identifier +
                                " - base: " + baseCode + ", quote: " + quoteCode);
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
