package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    @Autowired
    private CrossParityRepository crossParityRepository;

    public static class TradeCalculationRequest {
        private Integer crossParityId;
        private String operation; // "buy" or "sell"
        private double baseCurrencyMontant;
        private double quoteCurrencyMontant;
        private double price;

        // Getters and Setters
        public Integer getCrossParityId() {
            return crossParityId;
        }

        public void setCrossParityId(Integer crossParityId) {
            this.crossParityId = crossParityId;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public double getBaseCurrencyMontant() {
            return baseCurrencyMontant;
        }

        public void setBaseCurrencyMontant(double baseCurrencyMontant) {
            this.baseCurrencyMontant = baseCurrencyMontant;
        }

        public double getQuoteCurrencyMontant() {
            return quoteCurrencyMontant;
        }

        public void setQuoteCurrencyMontant(double quoteCurrencyMontant) {
            this.quoteCurrencyMontant = quoteCurrencyMontant;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    public static class TradeCalculationResponse {
        private double baseCurrencyMontant;
        private double quoteCurrencyMontant;

        public TradeCalculationResponse(double baseCurrencyMontant, double quoteCurrencyMontant) {
            this.baseCurrencyMontant = baseCurrencyMontant;
            this.quoteCurrencyMontant = quoteCurrencyMontant;
        }

        public double getBaseCurrencyMontant() {
            return baseCurrencyMontant;
        }

        public void setBaseCurrencyMontant(double baseCurrencyMontant) {
            this.baseCurrencyMontant = baseCurrencyMontant;
        }

        public double getQuoteCurrencyMontant() {
            return quoteCurrencyMontant;
        }

        public void setQuoteCurrencyMontant(double quoteCurrencyMontant) {
            this.quoteCurrencyMontant = quoteCurrencyMontant;
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<TradeCalculationResponse> calculateTradeAmounts(@RequestBody TradeCalculationRequest request) {
        // Validate inputs
//        if (!"buy".equalsIgnoreCase(request.getOperation()) && !"sell".equalsIgnoreCase(request.getOperation())) {
//            throw new RuntimeException("Operation must be 'buy' or 'sell'.");
//        }
        if (request.getBaseCurrencyMontant() == 0 && request.getQuoteCurrencyMontant() == 0) {
            throw new RuntimeException("Either baseCurrencyMontant or quoteCurrencyMontant must be provided.");
        }
        if (request.getBaseCurrencyMontant() > 0 && request.getQuoteCurrencyMontant() > 0) {
            throw new RuntimeException("Only one of baseCurrencyMontant or quoteCurrencyMontant can be provided.");
        }

        // Fetch CrossParity
        CrossParity crossParity = crossParityRepository.findById(request.getCrossParityId())
                .orElseThrow(() -> new RuntimeException("CrossParity not found"));

        double price = request.getPrice() > 0 ? request.getPrice() : crossParity.getRate();
        BigDecimal quotity = crossParity.getQuotity();
        double baseCurrencyMontant = request.getBaseCurrencyMontant();
        double quoteCurrencyMontant = request.getQuoteCurrencyMontant();

        // Validate quotity for baseCurrencyMontant
        if (baseCurrencyMontant > 0 && quotity != null && quotity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal baseAmountBd = BigDecimal.valueOf(baseCurrencyMontant);
            BigDecimal remainder = baseAmountBd.remainder(quotity);
            if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException("Base currency amount must be a multiple of quotity: " + quotity);
            }
        }

        // Calculate using the formula: quotity * baseCurrencyMontant = price * quoteCurrencyMontant
        if (baseCurrencyMontant > 0) {
            // quoteCurrencyMontant = (quotity * baseCurrencyMontant) / price
            quoteCurrencyMontant = (quotity.doubleValue() * baseCurrencyMontant) / price;
        } else {
            // baseCurrencyMontant = (price * quoteCurrencyMontant) / quotity
            baseCurrencyMontant = (price * quoteCurrencyMontant) / quotity.doubleValue();
        }

        // Round to 2 decimal places (adjust based on Currency.nbrDec if needed)
//        baseCurrencyMontant = BigDecimal.valueOf(baseCurrencyMontant)
//                .setScale(2, RoundingMode.HALF_UP)
//                .doubleValue();
//        quoteCurrencyMontant = BigDecimal.valueOf(quoteCurrencyMontant)
//                .setScale(2, RoundingMode.HALF_UP)
//                .doubleValue();

        TradeCalculationResponse response = new TradeCalculationResponse(baseCurrencyMontant, quoteCurrencyMontant);
        return ResponseEntity.ok(response);
    }
}