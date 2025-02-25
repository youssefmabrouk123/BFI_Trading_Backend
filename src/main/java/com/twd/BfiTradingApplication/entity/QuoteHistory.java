package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "quote_historie")
public class QuoteHistory {

    @EmbeddedId
    private QuoteHistoryId pk;

    @Column(precision = 19, scale = 4)
    private BigDecimal bidPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal askPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal spread;

    @Column(precision = 19, scale = 4)
    private BigDecimal netVar;

    @Column(precision = 19, scale = 4)
    private BigDecimal percentageVar;

    @ManyToOne
    @JoinColumn(name = "cross_parity_id")
    private CrossParity crossParity;

    // Constructeurs
    public QuoteHistory() {}

    public QuoteHistory(QuoteHistoryId pk, BigDecimal bidPrice, BigDecimal askPrice, BigDecimal spread,
                        BigDecimal netVar, BigDecimal percentageVar, CrossParity crossParity) {
        this.pk = pk;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
        this.spread = spread;
        this.netVar = netVar;
        this.percentageVar = percentageVar;
        this.crossParity = crossParity;
    }

    // Getters & Setters
    public QuoteHistoryId getPk() {
        return pk;
    }

    public void setPk(QuoteHistoryId pk) {
        this.pk = pk;
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

    public CrossParity getCrossParity() {
        return crossParity;
    }

    public void setCrossParity(CrossParity crossParity) {
        this.crossParity = crossParity;
    }
}
