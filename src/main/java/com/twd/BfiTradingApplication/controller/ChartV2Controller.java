package com.twd.BfiTradingApplication.controller;
import com.twd.BfiTradingApplication.dto.ChartDataDTO;
import com.twd.BfiTradingApplication.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@CrossOrigin(origins = "*") // Configure appropriately for production
public class ChartV2Controller {

    @Autowired
    private  ChartService chartService;

    @Autowired
    public ChartV2Controller(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/{parityId}/ma")
    public List<ChartDataDTO> getMovingAverage(
            @PathVariable Integer parityId,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "14") int period) {

        return chartService.calculateMovingAverage(parityId, days, period);
    }

    @GetMapping("/{parityId}/rsi")
    public List<ChartDataDTO> getRSI(
            @PathVariable Integer parityId,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "14") int period) {

        return chartService.calculateRSI(parityId, days, period);
    }
}