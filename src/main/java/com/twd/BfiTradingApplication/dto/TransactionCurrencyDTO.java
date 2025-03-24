package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionCurrencyDTO {
    private String currency;
    private Integer amount;
    private LocalDateTime transactionTime;

    public TransactionCurrencyDTO(String currency, Integer amount, LocalDateTime transactionTime) {
        this.currency = currency;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }
}

