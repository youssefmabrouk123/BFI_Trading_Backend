package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QuoteDTO {
    private Integer pk;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private BigDecimal spread;
    private BigDecimal netVar;
    private BigDecimal percentageVar;
    private LocalDateTime quoteTime;
    private String identifier ;
    private BigDecimal closeBid;
    private BigDecimal min;
    private BigDecimal max;
    private boolean favorite ;



}