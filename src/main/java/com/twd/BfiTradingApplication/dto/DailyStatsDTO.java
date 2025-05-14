package com.twd.BfiTradingApplication.dto;

import com.twd.BfiTradingApplication.entity.DailyStats;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO for response
public class DailyStatsDTO {
    private Integer pk;
    private Integer crossParityId;
    private LocalDate date;
    private BigDecimal maxBid;
    private BigDecimal minBid;
    private BigDecimal maxAsk;
    private BigDecimal minAsk;
    private BigDecimal openBid;
    private BigDecimal closeBid;
    private BigDecimal averageBid;
    private BigDecimal averageAsk;
    private BigDecimal volume;


    // Constructor
    public DailyStatsDTO(DailyStats stats) {
        this.pk = stats.getPk();
        this.crossParityId = stats.getCrossParity().getPk();
        this.date = stats.getDate();
        this.maxBid = stats.getMaxBid();
        this.minBid = stats.getMinBid();
        this.maxAsk = stats.getMaxAsk();
        this.minAsk = stats.getMinAsk();
        this.openBid = stats.getOpenBid();
        this.closeBid = stats.getCloseBid();
        this.averageBid = stats.getAverageBid();
        this.averageAsk = stats.getAverageAsk();
        this.volume = stats.getVolume();
    }

    // Getters
    public Integer getPk() { return pk; }
    public Integer getCrossParityId() { return crossParityId; }
    public LocalDate getDate() { return date; }
    public BigDecimal getMaxBid() { return maxBid; }
    public BigDecimal getMinBid() { return minBid; }
    public BigDecimal getMaxAsk() { return maxAsk; }
    public BigDecimal getMinAsk() { return minAsk; }
    public BigDecimal getOpenBid() { return openBid; }
    public BigDecimal getCloseBid() { return closeBid; }
    public BigDecimal getAverageBid() { return averageBid; }
    public BigDecimal getAverageAsk() { return averageAsk; }
    public BigDecimal getVolume() { return volume; }
}
