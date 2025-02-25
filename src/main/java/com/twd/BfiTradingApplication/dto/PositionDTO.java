package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class PositionDTO {
    private Integer pk;
    private Double openPrice;
    private Double currentPrice;
    private Double volume;
    private String type;
    private String status;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Double profitLoss;
    private Double stopLoss;
    private Double takeProfit;
    private Integer crossParityId;
    private String crossParityDescription;

}