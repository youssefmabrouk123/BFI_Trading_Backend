package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ChartDataDTO {
    private LocalDate date;
    private double value;
    private String label;

}