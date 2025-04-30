package com.twd.BfiTradingApplication.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CrossParityChartDTO {
    private LocalDate date;
    private BigDecimal openBid;
    private BigDecimal closeBid;
    private BigDecimal maxBid;
    private BigDecimal minBid;
    private BigDecimal openAsk;
    private BigDecimal closeAsk;
    private BigDecimal maxAsk;
    private BigDecimal minAsk;
    private BigDecimal spread;
    private BigDecimal volume;

    // Getters et Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpenBid() {
        return openBid;
    }

    public void setOpenBid(BigDecimal openBid) {
        this.openBid = openBid;
    }

    public BigDecimal getCloseBid() {
        return closeBid;
    }

    public void setCloseBid(BigDecimal closeBid) {
        this.closeBid = closeBid;
    }

    public BigDecimal getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(BigDecimal maxBid) {
        this.maxBid = maxBid;
    }

    public BigDecimal getMinBid() {
        return minBid;
    }

    public void setMinBid(BigDecimal minBid) {
        this.minBid = minBid;
    }

    public BigDecimal getOpenAsk() {
        return openAsk;
    }

    public void setOpenAsk(BigDecimal openAsk) {
        this.openAsk = openAsk;
    }

    public BigDecimal getCloseAsk() {
        return closeAsk;
    }

    public void setCloseAsk(BigDecimal closeAsk) {
        this.closeAsk = closeAsk;
    }

    public BigDecimal getMaxAsk() {
        return maxAsk;
    }

    public void setMaxAsk(BigDecimal maxAsk) {
        this.maxAsk = maxAsk;
    }

    public BigDecimal getMinAsk() {
        return minAsk;
    }

    public void setMinAsk(BigDecimal minAsk) {
        this.minAsk = minAsk;
    }

    public BigDecimal getSpread() {
        return spread;
    }

    public void setSpread(BigDecimal spread) {
        this.spread = spread;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}

