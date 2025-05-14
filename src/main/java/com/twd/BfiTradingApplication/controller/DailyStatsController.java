package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.DailyStatsDTO;
import com.twd.BfiTradingApplication.service.DailyStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-stats")
class DailyStatsController {


    @Autowired
    private  DailyStatsService service;



    @GetMapping("/cross-parity/{id}")
    public ResponseEntity<List<DailyStatsDTO>> getByCrossParityId(@PathVariable Integer id) {
        List<DailyStatsDTO> stats = service.getDailyStatsByCrossParityId(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/cross-parity/{id}/date")
    public ResponseEntity<List<DailyStatsDTO>> getByCrossParityIdAndDate(
            @PathVariable Integer id,
            @RequestParam("date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<DailyStatsDTO> stats = service.getDailyStatsByCrossParityIdAndDate(id, localDate);
        return ResponseEntity.ok(stats);
    }
}