package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProfitLossByDay {
    private LocalDateTime date;
    private BigDecimal profitLoss;
}