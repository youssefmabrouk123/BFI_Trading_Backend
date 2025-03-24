package com.twd.BfiTradingApplication.service;


import com.twd.BfiTradingApplication.dto.ChartDataDTO;
import com.twd.BfiTradingApplication.entity.DailyStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChartService {

    private final DailyStatsService dailyStatsService;
    private final QuoteHistoryService quoteHistoryService;

    @Autowired
    public ChartService(DailyStatsService dailyStatsService, QuoteHistoryService quoteHistoryService) {
        this.dailyStatsService = dailyStatsService;
        this.quoteHistoryService = quoteHistoryService;
    }

    /**
     * Calculate moving averages for a parity
     */
    public List<ChartDataDTO> calculateMovingAverage(Integer parityId, int days, int period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days + period); // Need extra days for calculation

        List<DailyStats> stats = dailyStatsService.findByParityIdAndDateRange(parityId, startDate, endDate);

        List<ChartDataDTO> result = new ArrayList<>();

        if (stats.size() < period) {
            return result; // Not enough data
        }

        // Group by date for easier access
        Map<LocalDate, DailyStats> statsByDate = stats.stream()
                .collect(Collectors.toMap(DailyStats::getDate, stat -> stat));

        for (int i = period; i < stats.size(); i++) {
            DailyStats currentStat = stats.get(i);
            double sum = 0;

            // Calculate sum for the period
            for (int j = 0; j < period; j++) {
                LocalDate date = currentStat.getDate().minusDays(j);
                if (statsByDate.containsKey(date)) {
                    sum += statsByDate.get(date).getCloseBid().doubleValue();
                }
            }

            // Calculate average
            double ma = sum / period;

            ChartDataDTO dto = new ChartDataDTO();
            dto.setDate(currentStat.getDate());
            dto.setValue(ma);
            dto.setLabel("MA" + period);

            result.add(dto);
        }

        return result;
    }

    /**
     * Calculate Relative Strength Index (RSI)
     */
    public List<ChartDataDTO> calculateRSI(Integer parityId, int days, int period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days + period * 2); // Need extra days for calculation

        List<DailyStats> stats = dailyStatsService.findByParityIdAndDateRange(parityId, startDate, endDate);

        List<ChartDataDTO> result = new ArrayList<>();

        if (stats.size() < period * 2) {
            return result; // Not enough data
        }

        // Calculate RSI
        for (int i = period; i < stats.size(); i++) {
            double avgGain = 0;
            double avgLoss = 0;

            // Calculate initial avg gain/loss
            for (int j = 1; j <= period; j++) {
                double change = stats.get(i - j + 1).getCloseBid().doubleValue() -
                        stats.get(i - j).getCloseBid().doubleValue();

                if (change > 0) {
                    avgGain += change;
                } else {
                    avgLoss += Math.abs(change);
                }
            }

            avgGain /= period;
            avgLoss /= period;

            // Calculate RSI
            double rs = avgGain / (avgLoss > 0 ? avgLoss : 0.001); // Avoid division by zero
            double rsi = 100 - (100 / (1 + rs));

            ChartDataDTO dto = new ChartDataDTO();
            dto.setDate(stats.get(i).getDate());
            dto.setValue(rsi);
            dto.setLabel("RSI" + period);

            result.add(dto);
        }

        return result;
    }
}
