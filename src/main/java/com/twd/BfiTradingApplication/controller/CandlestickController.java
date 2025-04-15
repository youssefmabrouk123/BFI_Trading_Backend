package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.CandlestickDTO;
import com.twd.BfiTradingApplication.service.CandlestickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candlesticks")
public class CandlestickController {

    private final CandlestickService candlestickService;

    @Autowired
    public CandlestickController(CandlestickService candlestickService) {
        this.candlestickService = candlestickService;
    }

    @GetMapping("/{crossParityId}")
    public ResponseEntity<List<CandlestickDTO>> getCandlesticks(
            @PathVariable Integer crossParityId,
            @RequestParam(defaultValue = "1h") String timeframe,
            @RequestParam(defaultValue = "100") Integer limit) {

        List<CandlestickDTO> candlesticks = candlestickService.getCandlesticks(crossParityId, timeframe, limit);
        return ResponseEntity.ok(candlesticks);
    }
}