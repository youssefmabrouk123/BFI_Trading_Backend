package com.twd.BfiTradingApplication.dto;
import java.math.BigDecimal;

public class CurrencyDetailDTO {
    private String currency;
    private String crossParity;
    private Integer positionId;
    private String role; // "Base" or "Quote"
    private String direction; // "Long" or "Short"
    private Integer quantity;
    private BigDecimal openPrice;
    private BigDecimal currentPrice;
    private BigDecimal profitLoss;

    // Constructor
    public CurrencyDetailDTO(String currency, String crossParity, Integer positionId, String role,
                             String direction, Integer quantity, BigDecimal openPrice,
                             BigDecimal currentPrice, BigDecimal profitLoss) {
        this.currency = currency;
        this.crossParity = crossParity;
        this.positionId = positionId;
        this.role = role;
        this.direction = direction;
        this.quantity = quantity;
        this.openPrice = openPrice;
        this.currentPrice = currentPrice;
        this.profitLoss = profitLoss;
    }

    // Getters and setters
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCrossParity() {
        return crossParity;
    }

    public void setCrossParity(String crossParity) {
        this.crossParity = crossParity;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
}