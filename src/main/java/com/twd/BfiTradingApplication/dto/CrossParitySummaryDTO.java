package com.twd.BfiTradingApplication.dto;
import java.math.BigDecimal;

public class CrossParitySummaryDTO {
    private Integer id;
    private String symbol;
    private int positionCount;
    private int totalQuantity;
    private BigDecimal netExposure;
    private BigDecimal currentPrice;
    private BigDecimal totalProfitLoss;

    // Constructor
    public CrossParitySummaryDTO(Integer id, String symbol, int positionCount, int totalQuantity,
                                 BigDecimal netExposure, BigDecimal currentPrice, BigDecimal totalProfitLoss) {
        this.id = id;
        this.symbol = symbol;
        this.positionCount = positionCount;
        this.totalQuantity = totalQuantity;
        this.netExposure = netExposure;
        this.currentPrice = currentPrice;
        this.totalProfitLoss = totalProfitLoss;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getNetExposure() {
        return netExposure;
    }

    public void setNetExposure(BigDecimal netExposure) {
        this.netExposure = netExposure;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getTotalProfitLoss() {
        return totalProfitLoss;
    }

    public void setTotalProfitLoss(BigDecimal totalProfitLoss) {
        this.totalProfitLoss = totalProfitLoss;
    }
}
