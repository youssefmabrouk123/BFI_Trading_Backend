package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.repository.*;
import com.twd.BfiTradingApplication.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class QuoteSimulationService {

     @Autowired
     private CrossParityRepository crossParityRepository;
     @Autowired
     private QuoteRepository quoteRepository;
     @Autowired
     private QuoteHistoryRepository quoteHistoryRepository;
     @Autowired
     private DailyStatsRepository dailyStatsRepository;
     @Autowired
     private DailyStatsService dailyStatsService;

     private final Random random = new Random();
     private static final double VOLATILITY_FACTOR = 0.0002; // Facteur de volatilité (0.02%)
     private static final double SPREAD_BASIS_POINTS = 0.0005; // Spread de base (0.05%)

     @Scheduled(fixedRate = 3000) // Toutes les 3 secondes
     public void simulateQuotes() {
          List<CrossParity> crossParities = crossParityRepository.findAll();

          for (CrossParity crossParity : crossParities) {
               // Utilise le rate de la CrossParity comme prix de base
               double baseRate = crossParity.getRate();

               // Génère le nouveau prix avec une volatilité réaliste
               BigDecimal bidPrice = generateRealisticPrice(baseRate);

               // Calcule l'ask price avec un spread dynamique
               BigDecimal spread = calculateDynamicSpread(bidPrice, crossParity);
               BigDecimal askPrice = bidPrice.add(spread);

               // Calcule les variations
               BigDecimal netVar = calculateNetVar(crossParity, bidPrice);
               BigDecimal percentageVar = calculatePercentageVar(crossParity, bidPrice);

               Quote quote = new Quote();
               quote=updateOrCreateQuote(crossParity, bidPrice, askPrice, spread, netVar, percentageVar);
               saveQuoteHistory(crossParity, bidPrice, askPrice, spread, netVar, percentageVar,quote);
               updateDailyStats(crossParity, bidPrice, askPrice);

          }
     }

     public BigDecimal generateRandomPrice() {
          double basePrice = 1.1000 + (random.nextDouble() * 0.0500);
          return BigDecimal.valueOf(basePrice).setScale(4, RoundingMode.HALF_UP);
     }

     private BigDecimal generateRealisticPrice(double baseRate) {
          // Génère une variation normale (distribution gaussienne)
          double gaussianDelta = random.nextGaussian();

          // Applique la variation au prix de base avec le facteur de volatilité
          double variation = gaussianDelta * VOLATILITY_FACTOR * baseRate;
          double newPrice = baseRate + variation;

          // Assure que le prix reste positif et arrondi à 4 décimales
          return BigDecimal.valueOf(Math.max(newPrice, 0.0001)).setScale(4, RoundingMode.HALF_UP);
     }

     private BigDecimal calculateDynamicSpread(BigDecimal bidPrice, CrossParity crossParity) {
          // Le spread de base est ajusté en fonction de la volatilité et de la liquidité
          double baseSpread = bidPrice.doubleValue() * SPREAD_BASIS_POINTS;

          // Ajoute une variation aléatoire au spread (±20% du spread de base)
          double spreadVariation = baseSpread * (0.8 + (random.nextDouble() * 0.4));

          return BigDecimal.valueOf(spreadVariation).setScale(4, RoundingMode.HALF_UP);
     }

     private Quote updateOrCreateQuote(CrossParity crossParity, BigDecimal bidPrice,
                                       BigDecimal askPrice, BigDecimal spread,
                                       BigDecimal netVar, BigDecimal percentageVar) {
          Quote existingQuote = quoteRepository.findByCrossParity(crossParity);

          if (existingQuote != null) {
               existingQuote.setBidPrice(bidPrice);
               existingQuote.setAskPrice(askPrice);
               existingQuote.setSpread(spread.multiply(BigDecimal.valueOf(10000)));
               existingQuote.setNetVar(netVar);
               existingQuote.setPercentageVar(percentageVar);
               existingQuote.setQuoteTime(LocalDateTime.now());
               quoteRepository.save(existingQuote);
               dailyStatsService.updateDailyStatsFromQuote(existingQuote);
               return existingQuote;
          } else {
               Quote newQuote = new Quote();
               newQuote.setCrossParity(crossParity);
               newQuote.setBidPrice(bidPrice);
               newQuote.setAskPrice(askPrice);
               newQuote.setSpread(spread.multiply(BigDecimal.valueOf(10000)));
               newQuote.setNetVar(netVar);
               newQuote.setPercentageVar(percentageVar);
               newQuote.setQuoteTime(LocalDateTime.now());
               quoteRepository.save(newQuote);
               dailyStatsService.updateDailyStatsFromQuote(newQuote);
               return newQuote;
          }
     }

     public BigDecimal calculateNetVar(CrossParity crossParity, BigDecimal newBidPrice) {
          Quote lastQuote = quoteRepository.findByCrossParity(crossParity);
          if (lastQuote != null) {
               return newBidPrice.subtract(lastQuote.getBidPrice());
          }
          return BigDecimal.ZERO;
     }

     public BigDecimal calculatePercentageVar(CrossParity crossParity, BigDecimal newBidPrice) {
          Quote lastQuote = quoteRepository.findByCrossParity(crossParity);
          if (lastQuote != null && lastQuote.getBidPrice().compareTo(BigDecimal.ZERO) > 0) {
               return newBidPrice.subtract(lastQuote.getBidPrice())
                       .divide(lastQuote.getBidPrice(), 4, RoundingMode.HALF_UP)
                       .multiply(new BigDecimal("100"));
          }
          return BigDecimal.ZERO;
     }

     public void saveQuoteHistory(CrossParity crossParity, BigDecimal bidPrice,
                                  BigDecimal askPrice, BigDecimal spread,
                                  BigDecimal netVar, BigDecimal percentageVar , Quote quote) {
          QuoteHistoryId quoteHistoryId = new QuoteHistoryId(quote.getPk(),quote.getQuoteTime());
          QuoteHistory quoteHistory = new QuoteHistory();
          quoteHistory.setPk(quoteHistoryId);
          quoteHistory.setCrossParity(crossParity);
          quoteHistory.setBidPrice(bidPrice);
          quoteHistory.setAskPrice(askPrice);
          quoteHistory.setSpread(spread);
          quoteHistory.setNetVar(netVar);
          quoteHistory.setPercentageVar(percentageVar);
          quoteHistoryRepository.save(quoteHistory);
     }

     public void updateDailyStats(CrossParity crossParity, BigDecimal bidPrice, BigDecimal askPrice) {
          LocalDate today = LocalDate.now();
          Optional<DailyStats> optionalDailyStats = dailyStatsRepository.findByCrossParityAndDate(crossParity, today);

          DailyStats dailyStats = optionalDailyStats.orElseGet(() -> {
               DailyStats newStats = new DailyStats();
               newStats.setCrossParity(crossParity);
               newStats.setDate(today);
               newStats.setOpenBid(bidPrice);
               newStats.setMaxBid(bidPrice);
               newStats.setMinBid(bidPrice);
               newStats.setMaxAsk(askPrice);
               newStats.setMinAsk(askPrice);
               return newStats;
          });

          dailyStats.setMaxBid(bidPrice.max(dailyStats.getMaxBid()));
          dailyStats.setMinBid(bidPrice.min(dailyStats.getMinBid()));
          dailyStats.setMaxAsk(askPrice.max(dailyStats.getMaxAsk()));
          dailyStats.setMinAsk(askPrice.min(dailyStats.getMinAsk()));
          dailyStats.setCloseBid(bidPrice);

          dailyStatsRepository.save(dailyStats);
     }
}