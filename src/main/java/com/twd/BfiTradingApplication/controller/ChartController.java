package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.CrossParityChartDTO;
import com.twd.BfiTradingApplication.dto.QuoteHistoryChartDTO;
import com.twd.BfiTradingApplication.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/charts")
public class ChartController {

    private final ChartService chartService;

    @Autowired
    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/cross-parity")
    public ResponseEntity<List<CrossParityChartDTO>> getCrossParityData(
            @RequestParam String identifier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusWeeks(1);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<CrossParityChartDTO> chartData = chartService.getCrossParityChartData(identifier, startDate, endDate);
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/cross-parity/intraday")
    public ResponseEntity<List<QuoteHistoryChartDTO>> getIntradayData(
            @RequestParam String identifier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        if (startDateTime == null) {
            startDateTime = LocalDateTime.now().minusHours(24);
        }

        if (endDateTime == null) {
            endDateTime = LocalDateTime.now();
        }

        List<QuoteHistoryChartDTO> chartData = chartService.getIntradayChartData(identifier, startDateTime, endDateTime);
        return ResponseEntity.ok(chartData);
    }
}