package com.twd.BfiTradingApplication.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    // The currency pair (e.g., USDJPY)
    @ManyToOne
    @JoinColumn(name = "cross_parity_id", nullable = false)
    private CrossParity crossParity;

    // Status of the position (e.g., OPEN, CLOSED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionStatus status;

    // Direction of the trade: true for Long, false for Short
    @Column(nullable = false)
    private boolean isLong;

    // Net quantity of the base currency (positive for long, negative for short)
    @Column(nullable = false)
    private Integer quantity;

    // Average price at which the position was opened
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal openPrice;

    // Timestamp when the position was first opened
    @Column(nullable = false)
    private LocalDateTime openTime;

    // Price at which the position was closed
    @Column(precision = 19, scale = 4)
    private BigDecimal closePrice;

    // Timestamp when the position was closed
    @Column
    private LocalDateTime closeTime;

    // Realized profit/loss on closed positions
    @Column(precision = 19, scale = 4)
    private BigDecimal realizedProfitLoss;


    @OneToMany(mappedBy = "position")
    private List<TransactionCurrency> transactionCurrencies;

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

    public PositionStatus getStatus() {
        return status;
    }

    public void setStatus(PositionStatus status) {
        this.status = status;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean isLong) {
        this.isLong = isLong;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalDateTime openTime) {
        this.openTime = openTime;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public BigDecimal getRealizedProfitLoss() {
        return realizedProfitLoss;
    }

    public void setRealizedProfitLoss(BigDecimal realizedProfitLoss) {
        this.realizedProfitLoss = realizedProfitLoss;
    }
}