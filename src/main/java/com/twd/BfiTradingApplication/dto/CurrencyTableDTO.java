package com.twd.BfiTradingApplication.dto;
import java.math.BigDecimal;

public class CurrencyTableDTO {
    private String currencyCode;
    private BigDecimal profitLoss;
    private BigDecimal longExposure;
    private BigDecimal shortExposure;
    private int totalPositions;

    public CurrencyTableDTO(String currencyCode, BigDecimal profitLoss,
                            BigDecimal longExposure, BigDecimal shortExposure, int totalPositions) {
        this.currencyCode = currencyCode;
        this.profitLoss = profitLoss;
        this.longExposure = longExposure;
        this.shortExposure = shortExposure;
        this.totalPositions = totalPositions;
    }

    // Calculate net exposure
    public BigDecimal getNetExposure() {
        return longExposure.subtract(shortExposure);
    }

    // Getters and setters
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    public BigDecimal getLongExposure() {
        return longExposure;
    }

    public void setLongExposure(BigDecimal longExposure) {
        this.longExposure = longExposure;
    }

    public BigDecimal getShortExposure() {
        return shortExposure;
    }

    public void setShortExposure(BigDecimal shortExposure) {
        this.shortExposure = shortExposure;
    }

    public int getTotalPositions() {
        return totalPositions;
    }

    public void setTotalPositions(int totalPositions) {
        this.totalPositions = totalPositions;
    }
}