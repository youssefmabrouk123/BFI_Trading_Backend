package com.twd.BfiTradingApplication.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.twd.BfiTradingApplication.dto.CandlestickDTO;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.repository.CrossParityRepository;
import com.twd.BfiTradingApplication.repository.QuoteHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CandlestickService {
    private static final Logger logger = LoggerFactory.getLogger(CandlestickService.class);

    private final QuoteHistoryRepository quoteHistoryRepository;
    private final CrossParityRepository crossParityRepository;
    private final SocketIOServer socketIOServer;

    @Autowired
    public CandlestickService(
            QuoteHistoryRepository quoteHistoryRepository,
            CrossParityRepository crossParityRepository,
            SocketIOServer socketIOServer) {
        this.quoteHistoryRepository = quoteHistoryRepository;
        this.crossParityRepository = crossParityRepository;
        this.socketIOServer = socketIOServer;

        // Setup socket.io events
        socketIOServer.addEventListener("requestCandlestickData", Map.class, (client, data, ackSender) -> {
            try {
                Integer crossParityId = (Integer) data.get("crossParityId");
                String timeframe = (String) data.get("timeframe");
                Integer limit = (Integer) data.getOrDefault("limit", 100);

                List<CandlestickDTO> candlesticks = getCandlesticks(crossParityId, timeframe, limit);
                client.sendEvent("candlestickData", candlesticks);
                logger.info("Sent {} candlesticks to client {}", candlesticks.size(), client.getSessionId());
            } catch (Exception e) {
                logger.error("Error processing candlestick request", e);
                client.sendEvent("error", "Failed to process candlestick request: " + e.getMessage());
            }
        });
    }

    /**
     * Get candlestick data for a specific cross parity and timeframe
     * @param crossParityId The ID of the cross parity
     * @param timeframe The timeframe (1m, 5m, 15m, 1h, 4h, 1d)
     * @param limit Maximum number of candlesticks to return
     * @return List of candlestick DTOs
     */
    public List<CandlestickDTO> getCandlesticks(Integer crossParityId, String timeframe, Integer limit) {
        CrossParity crossParity = crossParityRepository.findById(crossParityId)
                .orElseThrow(() -> new RuntimeException("Cross parity not found"));

        // Calculate time range based on timeframe and limit
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = calculateStartTime(endTime, timeframe, limit);

        // Get quote history data
        List<QuoteHistory> quoteHistories = quoteHistoryRepository
                .findByCrossParity_PkAndPk_QuoteTimeBetweenOrderByPk_QuoteTimeAsc(
                        crossParityId, startTime, endTime);

        // Group quote histories by time interval
        Map<LocalDateTime, List<QuoteHistory>> groupedQuotes = groupQuotesByTimeframe(quoteHistories, timeframe);

        // Convert grouped quotes to candlestick DTOs
        return groupedQuotes.entrySet().stream()
                .map(entry -> createCandlestickDTO(entry.getKey(), entry.getValue(), timeframe))
                .sorted(Comparator.comparing(CandlestickDTO::getTimestamp))
                .collect(Collectors.toList());
    }


    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void broadcastCandlestickUpdates() {
        try {
            // For each active cross parity, broadcast updates for common timeframes
            crossParityRepository.findAll().forEach(crossParity -> {
                for (String timeframe : Arrays.asList("1m", "5m", "15m", "1h")) {
                    List<CandlestickDTO> candlesticks = getCandlesticks(crossParity.getPk(), timeframe, 1);
                    if (!candlesticks.isEmpty()) {
                        socketIOServer.getBroadcastOperations().sendEvent(
                                "candlestickUpdate",
                                Map.of(
                                        "crossParityId", crossParity.getPk(),
                                        "timeframe", timeframe,
                                        "candlestick", candlesticks.get(candlesticks.size() - 1)
                                )
                        );
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error broadcasting candlestick updates", e);
        }
    }

    /**
     * Calculate the start time based on timeframe and limit
     */
    private LocalDateTime calculateStartTime(LocalDateTime endTime, String timeframe, int limit) {
        return switch (timeframe) {
            case "1m" -> endTime.minus(limit, ChronoUnit.MINUTES);
            case "5m" -> endTime.minus(limit * 5, ChronoUnit.MINUTES);
            case "15m" -> endTime.minus(limit * 15, ChronoUnit.MINUTES);
            case "1h" -> endTime.minus(limit, ChronoUnit.HOURS);
            case "4h" -> endTime.minus(limit * 4, ChronoUnit.HOURS);
            case "1d" -> endTime.minus(limit, ChronoUnit.DAYS);
            default -> throw new IllegalArgumentException("Invalid timeframe: " + timeframe);
        };
    }

    /**
     * Group quote histories by timeframe
     */
    private Map<LocalDateTime, List<QuoteHistory>> groupQuotesByTimeframe(
            List<QuoteHistory> quoteHistories, String timeframe) {

        Map<LocalDateTime, List<QuoteHistory>> groupedQuotes = new HashMap<>();

        for (QuoteHistory quote : quoteHistories) {
            LocalDateTime quoteTime = quote.getPk().getQuoteTime();
            LocalDateTime intervalStart = truncateToTimeframe(quoteTime, timeframe);

            groupedQuotes.computeIfAbsent(intervalStart, k -> new ArrayList<>()).add(quote);
        }

        return groupedQuotes;
    }

    /**
     * Truncate time to the beginning of the interval based on timeframe
     */
    private LocalDateTime truncateToTimeframe(LocalDateTime dateTime, String timeframe) {
        return switch (timeframe) {
            case "1m" -> dateTime.truncatedTo(ChronoUnit.MINUTES);
            case "5m" -> dateTime
                    .withMinute(dateTime.getMinute() - dateTime.getMinute() % 5)
                    .truncatedTo(ChronoUnit.MINUTES);
            case "15m" -> dateTime
                    .withMinute(dateTime.getMinute() - dateTime.getMinute() % 15)
                    .truncatedTo(ChronoUnit.MINUTES);
            case "1h" -> dateTime.truncatedTo(ChronoUnit.HOURS);
            case "4h" -> dateTime
                    .withHour(dateTime.getHour() - dateTime.getHour() % 4)
                    .truncatedTo(ChronoUnit.HOURS);
            case "1d" -> dateTime.truncatedTo(ChronoUnit.DAYS);
            default -> throw new IllegalArgumentException("Invalid timeframe: " + timeframe);
        };
    }

    /**
     * Create a candlestick DTO from a list of quote histories
     */
    private CandlestickDTO createCandlestickDTO(
            LocalDateTime intervalStart, List<QuoteHistory> quotes, String timeframe) {

        if (quotes.isEmpty()) {
            return null;
        }

        QuoteHistory first = quotes.get(0);
        QuoteHistory last = quotes.get(quotes.size() - 1);

        BigDecimal open = first.getBidPrice();
        BigDecimal close = last.getBidPrice();

        BigDecimal high = quotes.stream()
                .map(QuoteHistory::getBidPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal low = quotes.stream()
                .map(QuoteHistory::getBidPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Simple volume calculation (could be refined based on business requirements)
        BigDecimal volume = BigDecimal.valueOf(quotes.size());

        long timestamp = intervalStart.toEpochSecond(ZoneOffset.UTC) * 1000; // Convert to milliseconds

        return new CandlestickDTO(timestamp, open, high, low, close, volume);
    }
}