package com.twd.BfiTradingApplication.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuoteDTO {
    private Integer pk;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private BigDecimal spread;
    private BigDecimal netVar;
    private BigDecimal percentageVar;
    private LocalDateTime quoteTime;
    private String identifier ;
    private BigDecimal closeBid;
    private BigDecimal minBid;
    private BigDecimal maxAsk;
    private boolean favorite ;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }


    public BigDecimal getMaxAsk() {
        return maxAsk;
    }

    public void setMaxAsk(BigDecimal maxAsk) {
        this.maxAsk = maxAsk;
    }

    public BigDecimal getMinBid() {
        return minBid;
    }

    public void setMinBid(BigDecimal minBid) {
        this.minBid = minBid;
    }

    public BigDecimal getCloseBid() {
        return closeBid;
    }

    public void setCloseBid(BigDecimal closeBid) {
        this.closeBid = closeBid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    // Static inner class for CrossParity
//    public static class CrossParityDTO {
//        private String baseCurrency;
//        private String quoteCurrency;
//
//        // Getters and setters
//        public String getBaseCurrency() {
//            return baseCurrency;
//        }
//
//        public void setBaseCurrency(String baseCurrency) {
//            this.baseCurrency = baseCurrency;
//        }
//
//        public String getQuoteCurrency() {
//            return quoteCurrency;
//        }
//
//        public void setQuoteCurrency(String quoteCurrency) {
//            this.quoteCurrency = quoteCurrency;
//        }
//    }

    // Getters and setters
//    public CrossParityDTO getCrossParity() {
//        return crossParity;
//    }
//
//    public void setCrossParity(CrossParityDTO crossParity) {
//        this.crossParity = crossParity;
//    }

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

    public LocalDateTime getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(LocalDateTime quoteTime) {
        this.quoteTime = quoteTime;
    }
}