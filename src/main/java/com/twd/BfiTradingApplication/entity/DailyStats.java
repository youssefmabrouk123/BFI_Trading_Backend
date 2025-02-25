package com.twd.BfiTradingApplication.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_stat")
public class DailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @ManyToOne
    @JoinColumn(name = "cross_parity_id", nullable = false)
    @JsonBackReference
    private CrossParity crossParity;

    @Column(nullable = false)
    private LocalDate date;

    @Column(precision = 19, scale = 4)
    private BigDecimal maxBid;

    @Column(precision = 19, scale = 4)
    private BigDecimal minBid;

    @Column(precision = 19, scale = 4)
    private BigDecimal maxAsk;

    @Column(precision = 19, scale = 4)
    private BigDecimal minAsk;

    @Column(precision = 19, scale = 4)
    private BigDecimal openBid;

    @Column(precision = 19, scale = 4)
    private BigDecimal closeBid;

    @Column(precision = 19, scale = 4)
    private BigDecimal averageBid;

    @Column(precision = 19, scale = 4)
    private BigDecimal averageAsk;

    @Column(precision = 19, scale = 4)
    private BigDecimal volume;

    // Getters and Setters
    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public CrossParity getCrossParity() {
        return crossParity;
    }

    public void setCrossParity(CrossParity crossParity) {
        this.crossParity = crossParity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public BigDecimal getAverageBid() {
        return averageBid;
    }

    public void setAverageBid(BigDecimal averageBid) {
        this.averageBid = averageBid;
    }

    public BigDecimal getAverageAsk() {
        return averageAsk;
    }

    public void setAverageAsk(BigDecimal averageAsk) {
        this.averageAsk = averageAsk;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}