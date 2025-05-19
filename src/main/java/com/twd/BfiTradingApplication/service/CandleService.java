//package com.twd.BfiTradingApplication.service;
//
//import com.twd.BfiTradingApplication.dto.CandleDataDTO;
//import com.twd.BfiTradingApplication.dto.CrossParityDTO;
//import com.twd.BfiTradingApplication.entity.CrossParity;
//import com.twd.BfiTradingApplication.entity.DailyStats;
//import com.twd.BfiTradingApplication.entity.QuoteHistory;
//import com.twd.BfiTradingApplication.repository.CrossParityRepository;
//import com.twd.BfiTradingApplication.repository.DailyStatsRepository;
//import com.twd.BfiTradingApplication.repository.QuoteHistoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//public class CandleService {
//
//    @Autowired
//    private CrossParityRepository crossParityRepository;
//
//    @Autowired
//    private DailyStatsRepository dailyStatsRepository;
//
//    @Autowired
//    private QuoteHistoryRepository quoteHistoryRepository;
//
//    public List<CandleDataDTO> getCandleData(Integer crossParityId, LocalDate startDate, LocalDate endDate, String timeframe) {
//        // Vérifier que la cross parity existe
//        CrossParity crossParity = crossParityRepository.findById(crossParityId)
//                .orElseThrow(() -> new RuntimeException("Cross Parity not found"));
//
//        if ("DAILY".equals(timeframe)) {
//            // Utiliser les DailyStats pour les données journalières
//            List<DailyStats> dailyStats = dailyStatsRepository.findByCrossParityPkAndDateBetween(
//                    crossParityId, startDate, endDate);
//
//            return dailyStats.stream()
//                    .map(this::convertDailyStatsToCandleData)
//                    .collect(Collectors.toList());
//        } else {
//            // Pour les timeframes intraday, utiliser QuoteHistory
//            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
//            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
//
//            List<QuoteHistory> quoteHistories = quoteHistoryRepository
//                    .findByCrossParityPkAndQuoteTimeBetween(crossParityId, startDateTime, endDateTime);
//
//            return aggregateQuoteHistoryToCandles(quoteHistories, timeframe);
//        }
//    }
//
//    private CandleDataDTO convertDailyStatsToCandleData(DailyStats stats) {
//        CandleDataDTO candle = new CandleDataDTO();
//        candle.setDate(stats.getDate().toString());
//        candle.setOpen(stats.getOpenBid().doubleValue());
//        candle.setHigh(stats.getMaxBid().doubleValue());
//        candle.setLow(stats.getMinBid().doubleValue());
//        candle.setClose(stats.getCloseBid().doubleValue());
//        candle.setVolume(stats.getVolume() != null ? stats.getVolume().doubleValue() : 0);
//        return candle;
//    }
//
//    private List<CandleDataDTO> aggregateQuoteHistoryToCandles(List<QuoteHistory> quoteHistories, String timeframe) {
//        // Grouper les quotes par intervalle de temps selon le timeframe
//        Map<LocalDateTime, List<QuoteHistory>> groupedQuotes;
//
//        int minutes = getMinutesForTimeframe(timeframe);
//
//        groupedQuotes = quoteHistories.stream()
//                .collect(Collectors.groupingBy(q ->
//                        roundToTimeframe(q.getPk().getQuoteTime(), minutes)));
//
//        // Convertir chaque groupe en bougie (candle)
//        List<CandleDataDTO> candles = new ArrayList<>();
//
//        for (Map.Entry<LocalDateTime, List<QuoteHistory>> entry : groupedQuotes.entrySet()) {
//            List<QuoteHistory> quotes = entry.getValue();
//            if (quotes.isEmpty()) continue;
//
//            // Première quote pour l'ouverture
//            QuoteHistory openQuote = quotes.get(0);
//            // Dernière quote pour la clôture
//            QuoteHistory closeQuote = quotes.get(quotes.size() - 1);
//
//            // Calculer high, low
//            BigDecimal high = quotes.stream()
//                    .map(QuoteHistory::getBidPrice)
//                    .max(BigDecimal::compareTo)
//                    .orElse(BigDecimal.ZERO);
//
//            BigDecimal low = quotes.stream()
//                    .map(QuoteHistory::getBidPrice)
//                    .min(BigDecimal::compareTo)
//                    .orElse(BigDecimal.ZERO);
//
//            CandleDataDTO candle = new CandleDataDTO();
//            candle.setDate(entry.getKey().toString());
//            candle.setOpen(openQuote.getBidPrice().doubleValue());
//            candle.setHigh(high.doubleValue());
//            candle.setLow(low.doubleValue());
//            candle.setClose(closeQuote.getBidPrice().doubleValue());
//            // Volume est souvent non disponible dans les données forex, on utilise un placeholder
//            candle.setVolume(quotes.size());
//
//            candles.add(candle);
//        }
//
//        return candles;
//    }
//
//    private int getMinutesForTimeframe(String timeframe) {
//        switch (timeframe) {
//            case "M1": return 1;
//            case "M5": return 5;
//            case "M15": return 15;
//            case "M30": return 30;
//            case "H1": return 60;
//            case "H4": return 240;
//            default: return 1440; // Daily
//        }
//    }
//
//    private LocalDateTime roundToTimeframe(LocalDateTime time, int minuteInterval) {
//        int minute = time.getMinute();
//        int hour = time.getHour();
//        int roundedMinute = (minute / minuteInterval) * minuteInterval;
//
//        return time.withHour(hour).withMinute(roundedMinute).withSecond(0).withNano(0);
//    }
//
//    public List<CrossParityDTO> getAllCrossParities() {
//        List<CrossParity> crossParities = crossParityRepository.findAll();
//
//        return crossParities.stream()
//                .map(this::convertToCrossParityDTO)
//                .collect(Collectors.toList());
//    }
//
//    private CrossParityDTO convertToCrossParityDTO(CrossParity crossParity) {
//        CrossParityDTO dto = new CrossParityDTO();
//        dto.setId(crossParity.getPk());
//        dto.setIdentifier(crossParity.getIdentifier());
//        dto.setDescription(crossParity.getDescription());
//        dto.setBaseCurrency(crossParity.getBaseCurrency().getIdentifier());
//        dto.setQuoteCurrency(crossParity.getQuoteCurrency().getIdentifier());
//        return dto;
//    }
//}




package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.dto.CandleDataDTO;
import com.twd.BfiTradingApplication.dto.CrossParityDTO;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.DailyStats;
import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.DailyStatsRepository;
import com.twd.BfiTradingApplication.repository.QuoteHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandleService {

    @Autowired
    private CrossParityRepository crossParityRepository;

    @Autowired
    private DailyStatsRepository dailyStatsRepository;

    @Autowired
    private QuoteHistoryRepository quoteHistoryRepository;

    public List<CandleDataDTO> getCandleData(Integer crossParityId, LocalDate startDate, LocalDate endDate, String timeframe) {
        // Vérifier que la cross parity existe
        CrossParity crossParity = crossParityRepository.findById(crossParityId)
                .orElseThrow(() -> new RuntimeException("Cross Parity not found"));

        if ("DAILY".equals(timeframe)) {
            // Utiliser les DailyStats pour les données journalières
            List<DailyStats> dailyStats = dailyStatsRepository.findByCrossParityPkAndDateBetween(
                    crossParityId, startDate, endDate);

            return dailyStats.stream()
                    .map(this::convertDailyStatsToCandleData)
                    .collect(Collectors.toList());
        } else {
            // Pour les timeframes intraday, utiliser QuoteHistory
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

            List<QuoteHistory> quoteHistories = quoteHistoryRepository
                    .findByCrossParityPkAndQuoteTimeBetween(crossParityId, startDateTime, endDateTime);

            return aggregateQuoteHistoryToCandles(quoteHistories, timeframe);
        }
    }

    private CandleDataDTO convertDailyStatsToCandleData(DailyStats stats) {
        CandleDataDTO candle = new CandleDataDTO();
        candle.setDate(stats.getDate().toString());
        candle.setOpen(stats.getOpenBid().doubleValue());
        candle.setHigh(stats.getMaxBid().doubleValue());
        candle.setLow(stats.getMinBid().doubleValue());
        candle.setClose(stats.getCloseBid().doubleValue());
        candle.setVolume(stats.getVolume() != null ? stats.getVolume().doubleValue() : 0);
        return candle;
    }

    private List<CandleDataDTO> aggregateQuoteHistoryToCandles(List<QuoteHistory> quoteHistories, String timeframe) {
        // Sort quotes by quoteTime to ensure correct open/close values
        quoteHistories.sort(Comparator.comparing(q -> q.getPk().getQuoteTime()));

        // Grouper les quotes par intervalle de temps selon le timeframe
        Map<LocalDateTime, List<QuoteHistory>> groupedQuotes;

        int minutes = getMinutesForTimeframe(timeframe);

        groupedQuotes = quoteHistories.stream()
                .collect(Collectors.groupingBy(q ->
                        roundToTimeframe(q.getPk().getQuoteTime(), minutes)));

        // Convertir chaque groupe en bougie (candle)
        List<CandleDataDTO> candles = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<QuoteHistory>> entry : groupedQuotes.entrySet()) {
            List<QuoteHistory> quotes = entry.getValue();
            if (quotes.isEmpty()) continue;

            // Première quote pour l'ouverture
            QuoteHistory openQuote = quotes.get(0);
            // Dernière quote pour la clôture
            QuoteHistory closeQuote = quotes.get(quotes.size() - 1);

            // Calculer high, low
            BigDecimal high = quotes.stream()
                    .map(QuoteHistory::getBidPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            BigDecimal low = quotes.stream()
                    .map(QuoteHistory::getBidPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            CandleDataDTO candle = new CandleDataDTO();
            candle.setDate(entry.getKey().toString());
            candle.setOpen(openQuote.getBidPrice().doubleValue());
            candle.setHigh(high.doubleValue());
            candle.setLow(low.doubleValue());
            candle.setClose(closeQuote.getBidPrice().doubleValue());
            candle.setVolume((double) quotes.size());

            candles.add(candle);
        }

        return candles;
    }

    private int getMinutesForTimeframe(String timeframe) {
        switch (timeframe) {
            case "M1": return 1;
            case "M5": return 5;
            case "M15": return 15;
            case "M30": return 30;
            case "H1": return 60;
            case "H4": return 240;
            default: return 1440; // Daily
        }
    }

    private LocalDateTime roundToTimeframe(LocalDateTime time, int minuteInterval) {
        int minute = time.getMinute();
        int hour = time.getHour();
        int roundedMinute = (minute / minuteInterval) * minuteInterval;

        return time.withHour(hour).withMinute(roundedMinute).withSecond(0).withNano(0);
    }

    public List<CrossParityDTO> getAllCrossParities() {
        List<CrossParity> crossParities = crossParityRepository.findAll();

        return crossParities.stream()
                .map(this::convertToCrossParityDTO)
                .collect(Collectors.toList());
    }

    private CrossParityDTO convertToCrossParityDTO(CrossParity crossParity) {
        CrossParityDTO dto = new CrossParityDTO();
        dto.setId(crossParity.getPk());
        dto.setIdentifier(crossParity.getIdentifier());
        dto.setDescription(crossParity.getDescription());
        dto.setBaseCurrency(crossParity.getBaseCurrency().getIdentifier());
        dto.setQuoteCurrency(crossParity.getQuoteCurrency().getIdentifier());
        return dto;
    }
}
