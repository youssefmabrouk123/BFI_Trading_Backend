package com.twd.BfiTradingApplication.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClosedPositionDTO {
    private Integer id;
    private String crossParityName;
    private String direction;
    private Integer quantity;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private BigDecimal realizedProfitLoss;
    private double holdingPeriodHours;

    public ClosedPositionDTO(Integer id, String crossParityName, String direction, Integer quantity,
                             BigDecimal openPrice, BigDecimal closePrice, LocalDateTime openTime,
                             LocalDateTime closeTime, BigDecimal realizedProfitLoss, double holdingPeriodHours) {
        this.id = id;
        this.crossParityName = crossParityName;
        this.direction = direction;
        this.quantity = quantity;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.realizedProfitLoss = realizedProfitLoss;
        this.holdingPeriodHours = holdingPeriodHours;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCrossParityName() {
        return crossParityName;
    }

    public void setCrossParityName(String crossParityName) {
        this.crossParityName = crossParityName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalDateTime openTime) {
        this.openTime = openTime;
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

    public double getHoldingPeriodHours() {
        return holdingPeriodHours;
    }

    public void setHoldingPeriodHours(double holdingPeriodHours) {
        this.holdingPeriodHours = holdingPeriodHours;
    }
}
