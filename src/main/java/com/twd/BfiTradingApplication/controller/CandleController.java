package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.CandleDataDTO;
import com.twd.BfiTradingApplication.dto.CrossParityDTO;
import com.twd.BfiTradingApplication.service.CandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/candles")
public class CandleController {

    @Autowired
    private CandleService candleService;

    @GetMapping("/{crossParityId}")
    public ResponseEntity<List<CandleDataDTO>> getCandleData(
            @PathVariable Integer crossParityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") String timeframe) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<CandleDataDTO> candleData = candleService.getCandleData(crossParityId, startDate, endDate, timeframe);
        return ResponseEntity.ok(candleData);
    }

    @GetMapping("/cross-parities")
    public ResponseEntity<List<CrossParityDTO>> getAllCrossParities() {
        return ResponseEntity.ok(candleService.getAllCrossParities());
    }
}