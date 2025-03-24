package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.dto.ClosedPositionDTO;
import com.twd.BfiTradingApplication.dto.CrossParitySummaryDTO;
import com.twd.BfiTradingApplication.dto.CurrencyDetailDTO;
import com.twd.BfiTradingApplication.dto.CurrencyTableDTO;
import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.PositionRepository;
import com.twd.BfiTradingApplication.repository.TransactionCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CrossParityRepository crossParityRepository;

    @Autowired
    private TransactionCurrencyRepository transactionCurrencyRepository;

    // Open or update a position
    @Transactional
    public Position openPosition(Integer crossParityId, boolean isLong, Integer quantity, BigDecimal openPrice) {
        CrossParity crossParity = crossParityRepository.findById(crossParityId)
                .orElseThrow(() -> new IllegalArgumentException("CrossParity not found with ID: " + crossParityId));

        // Calculate actual quantity to add or subtract
        int actualQuantity = isLong ? quantity : -quantity;

        // Find existing position for this cross parity
        Optional<Position> existingPositionOpt = positionRepository.findByCrossParityAndStatus(
                crossParity, PositionStatus.OPEN);

        if (existingPositionOpt.isPresent()) {
            // Update existing position
            Position existingPosition = existingPositionOpt.get();

            // Calculate new net quantity
            int newQuantity = existingPosition.getQuantity() + actualQuantity;

            // If new quantity is zero, close the position
            if (newQuantity == 0) {
                existingPosition.setStatus(PositionStatus.CLOSED);
                existingPosition.setClosePrice(openPrice);
                existingPosition.setCloseTime(LocalDateTime.now());
                existingPosition.setRealizedProfitLoss(calculateClosingProfitLoss(
                        existingPosition, openPrice));
                return positionRepository.save(existingPosition);
            }

            // Calculate weighted average price
            BigDecimal existingValue = existingPosition.getOpenPrice()
                    .multiply(BigDecimal.valueOf(Math.abs(existingPosition.getQuantity())));
            BigDecimal newValue = openPrice.multiply(BigDecimal.valueOf(Math.abs(actualQuantity)));
            BigDecimal totalValue = existingValue.add(newValue);
            BigDecimal avgPrice = totalValue.divide(
                    BigDecimal.valueOf(Math.abs(existingPosition.getQuantity()) + Math.abs(actualQuantity)),
                    4, BigDecimal.ROUND_HALF_UP);

            // Update direction based on sign
            boolean newIsLong = newQuantity > 0;

            // If direction changed, reset average price to current price
            if (newIsLong != existingPosition.isLong()) {
                avgPrice = openPrice;
            }

            existingPosition.setQuantity(newQuantity);
            existingPosition.setOpenPrice(avgPrice);
            existingPosition.setLong(newIsLong);

            return positionRepository.save(existingPosition);
        } else {
            // Create a new position if none exists
            Position position = new Position();
            position.setCrossParity(crossParity);
            position.setStatus(PositionStatus.OPEN);
            position.setLong(actualQuantity > 0); // Set direction based on sign of quantity
            position.setQuantity(actualQuantity);
            position.setOpenPrice(openPrice);
            position.setOpenTime(LocalDateTime.now());

            return positionRepository.save(position);
        }
    }

    // Helper method to calculate P/L when closing a position
    private BigDecimal calculateClosingProfitLoss(Position position, BigDecimal closePrice) {
        BigDecimal openPrice = position.getOpenPrice();
        Integer quantity = position.getQuantity();

        // Calculate price difference
        BigDecimal priceDifference = position.isLong()
                ? closePrice.subtract(openPrice) // Long: profit if price increases
                : openPrice.subtract(closePrice); // Short: profit if price decreases

        // Calculate P/L in quote currency
        return priceDifference.multiply(BigDecimal.valueOf(Math.abs(quantity)));
    }

    // Manually close a position with closing price
    @Transactional
    public Position closePosition(Integer positionId, BigDecimal closePrice) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Position not found with ID: " + positionId));

        if (position.getStatus() == PositionStatus.CLOSED) {
            throw new IllegalStateException("Position is already closed");
        }

        position.setStatus(PositionStatus.CLOSED);
        position.setClosePrice(closePrice);
        position.setCloseTime(LocalDateTime.now());

        // Calculate and set realized profit/loss
        position.setRealizedProfitLoss(calculateClosingProfitLoss(position, closePrice));

        // --- TRANSACTION ENTRIES FOR CLOSING ---
        List<TransactionCurrency> transactions = new ArrayList<>();

        // Reverse BASE Currency transaction (EUR)
        TransactionCurrency baseTransaction = new TransactionCurrency();
        baseTransaction.setPosition(position);
        baseTransaction.setCurrency(position.getCrossParity().getBaseCurrency());
        baseTransaction.setAmount(- position.getQuantity()); // Negate manually
        transactions.add(baseTransaction);

        // Reverse QUOTE Currency transaction (USD)
        TransactionCurrency quoteTransaction = new TransactionCurrency();
        quoteTransaction.setPosition(position);
        quoteTransaction.setCurrency(position.getCrossParity().getQuoteCurrency());

        // Multiply using BigDecimal and convert to Integer
        Integer quoteAmount = closePrice.multiply(BigDecimal.valueOf(position.getQuantity())).intValue();
        quoteTransaction.setAmount(quoteAmount);
        transactions.add(quoteTransaction);

        transactionCurrencyRepository.saveAll(transactions);

        return positionRepository.save(position);
    }

    // Calculate Profit/Loss for an open position (unrealized)
//    public BigDecimal calculateProfitLoss(Position position) {
//        if (position.getStatus() != PositionStatus.OPEN) {
//            // For closed positions, return the realized P/L if available
//            return position.getRealizedProfitLoss() != null ? position.getRealizedProfitLoss() : BigDecimal.ZERO;
//        }
//
//        // Get the current price from the Quote entity
//        Quote currentQuote = position.getCrossParity().getQuote();
//        if (currentQuote == null) {
//            return BigDecimal.ZERO; // No current price available
//        }
//
//        BigDecimal currentPrice = currentQuote.getBidPrice(); // Use bid price for simplicity
//        BigDecimal openPrice = position.getOpenPrice();
//        Integer quantity = position.getQuantity();
//
//        // Calculate price difference
//        BigDecimal priceDifference = position.isLong()
//                ? currentPrice.subtract(openPrice) // Long: profit if price increases
//                : openPrice.subtract(currentPrice); // Short: profit if price decreases
//
//        // Calculate P/L in quote currency (e.g., JPY for USDJPY)
//        BigDecimal profitLoss = priceDifference.multiply(BigDecimal.valueOf(Math.abs(quantity)));
//
//        return profitLoss;
//    }

    public BigDecimal calculateProfitLoss(Position position) {
        if (position.getStatus() != PositionStatus.OPEN) {
            // Pour les positions fermées, retourner le P/L réalisé si disponible
            return position.getRealizedProfitLoss() != null ? position.getRealizedProfitLoss() : BigDecimal.ZERO;
        }

        // Obtenir la cotation actuelle à partir de l'entité Quote
        Quote currentQuote = position.getCrossParity().getQuote();
        if (currentQuote == null) {
            return BigDecimal.ZERO; // Pas de prix actuel disponible
        }

        BigDecimal currentPrice;
        if (position.isLong()) {
            currentPrice = currentQuote.getBidPrice(); // Position longue : utiliser le prix Bid
        } else {
            currentPrice = currentQuote.getAskPrice(); // Position courte : utiliser le prix Ask
        }

        BigDecimal openPrice = position.getOpenPrice();
        Integer quantity = position.getQuantity();

        // Calculer la différence de prix
        BigDecimal priceDifference = position.isLong()
                ? currentPrice.subtract(openPrice) // Longue : profit si le prix augmente
                : openPrice.subtract(currentPrice); // Courte : profit si le prix diminue

        // Calculer le P/L en devise de cotation (par exemple, JPY pour USD/JPY)
        BigDecimal profitLoss = priceDifference.multiply(BigDecimal.valueOf(Math.abs(quantity)));

        return profitLoss;
    }



    // Get cross parity summary (one row per cross parity)
    public List<CrossParitySummaryDTO> getCrossParitySummary() {
        List<CrossParity> allCrossParity = crossParityRepository.findAll();

        return allCrossParity.stream().map(crossParity -> {
            // Get the open position for this cross parity (at most one)
            Optional<Position> openPositionOpt = positionRepository.findByCrossParityAndStatus(
                    crossParity, PositionStatus.OPEN);

            // Default values
            BigDecimal totalProfitLoss = BigDecimal.ZERO;
            int totalQuantity = 0;
            BigDecimal netExposure = BigDecimal.ZERO;
            int positionCount = 0;

            // If position exists, calculate values
            if (openPositionOpt.isPresent()) {
                Position position = openPositionOpt.get();
                totalProfitLoss = calculateProfitLoss(position);
                totalQuantity = Math.abs(position.getQuantity());
                netExposure = BigDecimal.valueOf(position.getQuantity());
                positionCount = 1;
            }

            // Current price
            BigDecimal currentPrice = crossParity.getQuote() != null
                    ? crossParity.getQuote().getBidPrice()
                    : BigDecimal.ZERO;

            return new CrossParitySummaryDTO(
                    crossParity.getPk(),
                    crossParity.getIdentifier(),
                    positionCount,
                    totalQuantity,
                    netExposure,
                    currentPrice,
                    totalProfitLoss
            );
        }).collect(Collectors.toList());
    }

    // Get currency details (multiple rows per currency)
    public List<CurrencyDetailDTO> getCurrencyDetails() {
        List<Position> openPositions = positionRepository.findByStatus(PositionStatus.OPEN);

        // Group positions by currency code
        Map<String, List<Position>> currencyPositions = new HashMap<>();

        for (Position position : openPositions) {
            String baseCurrencyCode = position.getCrossParity().getBaseCurrency().getIdentifier();
            String quoteCurrencyCode = position.getCrossParity().getQuoteCurrency().getIdentifier();

            // Add position to base currency list
            if (!currencyPositions.containsKey(baseCurrencyCode)) {
                currencyPositions.put(baseCurrencyCode, new ArrayList<>());
            }
            currencyPositions.get(baseCurrencyCode).add(position);

            // Also add position to quote currency list
            if (!currencyPositions.containsKey(quoteCurrencyCode)) {
                currencyPositions.put(quoteCurrencyCode, new ArrayList<>());
            }
            currencyPositions.get(quoteCurrencyCode).add(position);
        }

        // Create detail DTOs
        List<CurrencyDetailDTO> currencyDetails = new ArrayList<>();

        for (Map.Entry<String, List<Position>> entry : currencyPositions.entrySet()) {
            String currencyCode = entry.getKey();
            List<Position> positions = entry.getValue();

            for (Position position : positions) {
                // Check if this position is related to the current currency
                boolean isBaseCurrency = position.getCrossParity().getBaseCurrency().getIdentifier().equals(currencyCode);

                // Create a detail entry for each position
                currencyDetails.add(new CurrencyDetailDTO(
                        currencyCode,
                        position.getCrossParity().getIdentifier(),
                        position.getPk(),
                        isBaseCurrency ? "Base" : "Quote",
                        position.isLong() ? "Long" : "Short",
                        isBaseCurrency ? position.getQuantity() :
                                position.getQuantity() * -1, // Invert for quote currency
                        position.getOpenPrice(),
                        position.getCrossParity().getQuote() != null ?
                                position.getCrossParity().getQuote().getBidPrice() : BigDecimal.ZERO,
                        calculateProfitLoss(position)
                ));
            }
        }

        return currencyDetails;
    }

    // Get currency table (one row per currency with aggregated data)
    public List<CurrencyTableDTO> getCurrencyTable() {
        List<Position> openPositions = positionRepository.findByStatus(PositionStatus.OPEN);
        Map<String, CurrencyTableDTO> currencyMap = new HashMap<>();

        // Process all open positions
        for (Position position : openPositions) {
            String baseCurrencyCode = position.getCrossParity().getBaseCurrency().getIdentifier();
            String quoteCurrencyCode = position.getCrossParity().getQuoteCurrency().getIdentifier();

            // Process base currency
            processCurrency(currencyMap, baseCurrencyCode, position, true);

            // Process quote currency
            processCurrency(currencyMap, quoteCurrencyCode, position, false);
        }

        return new ArrayList<>(currencyMap.values());
    }

    // Helper method to process a currency for the currency table
    private void processCurrency(Map<String, CurrencyTableDTO> currencyMap, String currencyCode,
                                 Position position, boolean isBaseCurrency) {

        // Get or create currency table entry
        CurrencyTableDTO currencyDTO = currencyMap.getOrDefault(currencyCode,
                new CurrencyTableDTO(currencyCode, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0));

        // Calculate exposure based on whether it's base or quote currency
        int quantity = Math.abs(position.getQuantity());
        BigDecimal positionPL = calculateProfitLoss(position);

        if (isBaseCurrency) {
            // For base currency
            currencyDTO.setTotalPositions(currencyDTO.getTotalPositions() + 1);

            if (position.isLong()) {
                currencyDTO.setLongExposure(currencyDTO.getLongExposure().add(BigDecimal.valueOf(quantity)));
            } else {
                currencyDTO.setShortExposure(currencyDTO.getShortExposure().add(BigDecimal.valueOf(quantity)));
            }

            // Add P/L directly for base currency
            currencyDTO.setProfitLoss(currencyDTO.getProfitLoss().add(positionPL));

        } else {
            // For quote currency - need to convert values
            currencyDTO.setTotalPositions(currencyDTO.getTotalPositions() + 1);

            // Convert quantity to quote currency value
            BigDecimal quoteValue = BigDecimal.valueOf(quantity).multiply(position.getOpenPrice());

            // Quote positions are opposite of base positions
            if (!position.isLong()) {
                currencyDTO.setLongExposure(currencyDTO.getLongExposure().add(quoteValue));
            } else {
                currencyDTO.setShortExposure(currencyDTO.getShortExposure().add(quoteValue));
            }

            // Convert P/L to quote currency
            BigDecimal quotePL = positionPL.divide(position.getOpenPrice(), 4, BigDecimal.ROUND_HALF_UP);
            currencyDTO.setProfitLoss(currencyDTO.getProfitLoss().add(quotePL));
        }

        // Update the map
        currencyMap.put(currencyCode, currencyDTO);
    }

    // Get all open positions with their P/L
    public List<PositionDTO> getOpenPositionsWithProfitLoss() {
        List<Position> openPositions = positionRepository.findByStatus(PositionStatus.OPEN);
        return openPositions.stream().map(position -> {
            BigDecimal profitLoss = calculateProfitLoss(position);
            return new PositionDTO(
                    position.getPk(),
                    position.getCrossParity().getIdentifier(),
                    position.getStatus().toString(),
                    position.isLong() ? "Long" : "Short",
                    position.getQuantity(),
                    position.getOpenPrice(),
                    position.getCrossParity().getQuote() != null
                            ? (position.isLong()
                            ? position.getCrossParity().getQuote().getBidPrice()
                            : position.getCrossParity().getQuote().getAskPrice())
                            : BigDecimal.ZERO,
                    profitLoss
            );

        }).toList();
    }

    // Get all closed positions with their realized P/L
    public List<ClosedPositionDTO> getClosedPositions() {
        List<Position> closedPositions = positionRepository.findByStatus(PositionStatus.CLOSED);
        return closedPositions.stream().map(position -> {
            return new ClosedPositionDTO(
                    position.getPk(),
                    position.getCrossParity().getIdentifier(),
                    position.isLong() ? "Long" : "Short",
                    position.getQuantity(),
                    position.getOpenPrice(),
                    position.getClosePrice(),
                    position.getOpenTime(),
                    position.getCloseTime(),
                    position.getRealizedProfitLoss(),
                    calculateHoldingPeriodInHours(position.getOpenTime(), position.getCloseTime())
            );
        }).toList();
    }

    // Calculate holding period in hours
    private double calculateHoldingPeriodInHours(LocalDateTime openTime, LocalDateTime closeTime) {
        if (openTime == null || closeTime == null) {
            return 0;
        }

        long seconds = java.time.Duration.between(openTime, closeTime).getSeconds();
        return seconds / 3600.0; // Convert to hours
    }


    public BigDecimal getLatestPrice(Integer positionId) {
        // Fetch the position by ID
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Position not found with ID: " + positionId));

        // Get the current price from the Quote entity
        Quote currentQuote = position.getCrossParity().getQuote();
        if (currentQuote == null) {
            throw new IllegalStateException("No current price available for the position's cross parity");
        }

        // Return the bid price as the latest price
        return currentQuote.getBidPrice();
    }


//    aaaaa

    public List<Position> findByParityId(Integer parityId) {
        return positionRepository.findByParityId(parityId);
    }

    public List<Position> findByParityIdAndStatus(Integer parityId, PositionStatus status) {
        return positionRepository.findByParityIdAndStatus(parityId, status);
    }

    public Optional<Position> findById(Integer id) {
        return positionRepository.findById(id);
    }

    public Position save(Position position) {
        return positionRepository.save(position);
    }

    public void deleteById(Integer id) {
        positionRepository.deleteById(id);
    }

//    aaaaa
}
