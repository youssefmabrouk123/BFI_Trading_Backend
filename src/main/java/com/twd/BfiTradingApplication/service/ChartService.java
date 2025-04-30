package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.dto.CrossParityChartDTO;
import com.twd.BfiTradingApplication.dto.QuoteHistoryChartDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChartService {

    private final CrossParityRepository crossParityRepository;
    private final DailyStatsRepository dailyStatsRepository;
    private final QuoteHistoryRepository quoteHistoryRepository;

    @Autowired
    public ChartService(
            CrossParityRepository crossParityRepository,
            DailyStatsRepository dailyStatsRepository,
            QuoteHistoryRepository quoteHistoryRepository) {
        this.crossParityRepository = crossParityRepository;
        this.dailyStatsRepository = dailyStatsRepository;
        this.quoteHistoryRepository = quoteHistoryRepository;
    }

    public List<CrossParityChartDTO> getCrossParityChartData(String identifier, LocalDate startDate, LocalDate endDate) {
        CrossParity crossParity = crossParityRepository.findByIdentifier(identifier);

        if (crossParity == null) {
            return new ArrayList<>();
        }

        List<DailyStats> dailyStats = dailyStatsRepository.findByCrossParityAndDateBetweenOrderByDateAsc(
                crossParity, startDate, endDate);

        return dailyStats.stream().map(this::convertToCrossParityChartDTO).collect(Collectors.toList());
    }

    public List<QuoteHistoryChartDTO> getIntradayChartData(String identifier, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        CrossParity crossParity = crossParityRepository.findByIdentifier(identifier);

        if (crossParity == null) {
            return new ArrayList<>();
        }

        List<QuoteHistory> quoteHistories = quoteHistoryRepository.findByCrossParityAndQuoteTimeBetweenOrderByQuoteTimeAsc(
                crossParity, startDateTime, endDateTime);

        return quoteHistories.stream().map(this::convertToQuoteHistoryChartDTO).collect(Collectors.toList());
    }

    private CrossParityChartDTO convertToCrossParityChartDTO(DailyStats dailyStats) {
        CrossParityChartDTO dto = new CrossParityChartDTO();
        dto.setDate(dailyStats.getDate());
        dto.setOpenBid(dailyStats.getOpenBid());
        dto.setCloseBid(dailyStats.getCloseBid());
        dto.setMaxBid(dailyStats.getMaxBid());
        dto.setMinBid(dailyStats.getMinBid());

        // Calcul des valeurs Ask à partir des valeurs Bid et du spread moyen
        BigDecimal averageSpread = dailyStats.getAverageAsk().subtract(dailyStats.getAverageBid());

        BigDecimal openAsk = dailyStats.getOpenBid().add(averageSpread);
        dto.setOpenAsk(openAsk);

        BigDecimal closeAsk = dailyStats.getCloseBid().add(averageSpread);
        dto.setCloseAsk(closeAsk);

        dto.setMaxAsk(dailyStats.getMaxAsk());
        dto.setMinAsk(dailyStats.getMinAsk());

        dto.setSpread(averageSpread);
        dto.setVolume(dailyStats.getVolume());

        return dto;
    }

    private QuoteHistoryChartDTO convertToQuoteHistoryChartDTO(QuoteHistory quoteHistory) {
        QuoteHistoryChartDTO dto = new QuoteHistoryChartDTO();
        dto.setQuoteTime(quoteHistory.getPk().getQuoteTime());
        dto.setBidPrice(quoteHistory.getBidPrice());
        dto.setAskPrice(quoteHistory.getAskPrice());
        dto.setSpread(quoteHistory.getSpread());
        dto.setNetVar(quoteHistory.getNetVar());
        dto.setPercentageVar(quoteHistory.getPercentageVar());

        return dto;
    }
}