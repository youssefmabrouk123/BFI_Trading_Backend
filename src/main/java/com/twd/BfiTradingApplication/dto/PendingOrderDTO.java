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
    private String orderType;
    private String triggerType;
    private String actionOnTrigger;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    private LocalDateTime expiresAt;
    private String duration;
    private String errorMessage; // e.g., "30_MINUTES", "1_HOUR", "1_DAY"
}