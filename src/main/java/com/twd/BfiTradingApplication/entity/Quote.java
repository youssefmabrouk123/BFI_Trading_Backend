package com.twd.SpringSecurityJWT.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

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

    private LocalDateTime quoteTime;

    // Relation One-to-One avec CrossParity
    @OneToOne
    @JoinColumn(name = "cross_parity_id", unique = true)
    private CrossParity crossParity;

    // Constructeurs
    public Quote() {
    }

    public Quote(BigDecimal bidPrice, BigDecimal askPrice, BigDecimal spread, BigDecimal netVar, BigDecimal percentageVar, LocalDateTime quoteTime) {
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
        this.spread = spread;
        this.netVar = netVar;
        this.percentageVar = percentageVar;
        this.quoteTime = quoteTime;
    }

    // Getters & Setters
    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
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

    public LocalDateTime getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(LocalDateTime quoteTime) {
        this.quoteTime = quoteTime;
    }

    public CrossParity getCrossParity() {
        return crossParity;
    }

    public void setCrossParity(CrossParity crossParity) {
        this.crossParity = crossParity;
    }
}