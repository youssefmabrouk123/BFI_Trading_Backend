package com.twd.BfiTradingApplication.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuoteHistoryChartDTO {
    private LocalDateTime quoteTime;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private BigDecimal spread;
    private BigDecimal netVar;
    private BigDecimal percentageVar;

    // Getters et Setters
    public LocalDateTime getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(LocalDateTime quoteTime) {
        this.quoteTime = quoteTime;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(BigDecimal askPrice) {
        this.askPrice = askPrice;
    }

    public BigDecimal getSpread() {
        return spread;
    }

    public void setSpread(BigDecimal spread) {
        this.spread = spread;
    }

    public BigDecimal getNetVar() {
        return netVar;
    }

    public void setNetVar(BigDecimal netVar) {
        this.netVar = netVar;
    }

    public BigDecimal getPercentageVar() {
        return percentageVar;
    }

    public void setPercentageVar(BigDecimal percentageVar) {
        this.percentageVar = percentageVar;
    }
}