package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PendingOrderDTO {
    private Integer id;
    private Integer baseCurrencyId;
    private String baseCurrencyIdentifier;
    private Integer quoteCurrencyId;
    private String quoteCurrencyIdentifier;
    private BigDecimal amount;
    private BigDecimal targetPrice;
    private String orderType; // "BUY" or "SELL"
    private String triggerType; // "STOP_LOSS" or "TAKE_PROFIT"
    private String actionOnTrigger; // "EXECUTE" or "NOTIFY"
    private String status; // "PENDING", "EXECUTED", "CANCELLED"
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
}