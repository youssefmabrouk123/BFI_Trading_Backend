package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.DailyStats;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.service.CrossParityService;
import com.twd.BfiTradingApplication.service.DailyStatsService;
import com.twd.BfiTradingApplication.service.PositionService;
import com.twd.BfiTradingApplication.service.QuoteHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/public/api/cross-parities")
public class CrossParityController {

//    @Autowired
//    private CrossParityService crossParityService;

//    aaaa

    private final CrossParityService crossParityService;
    private final QuoteHistoryService quoteHistoryService;
    private final DailyStatsService dailyStatsService;
    private final PositionService positionService;

    @Autowired
    public CrossParityController(CrossParityService crossParityService,
                                 QuoteHistoryService quoteHistoryService,
                                 DailyStatsService dailyStatsService,
                                 PositionService positionService) {
        this.crossParityService = crossParityService;
        this.quoteHistoryService = quoteHistoryService;
        this.dailyStatsService = dailyStatsService;
        this.positionService = positionService;
    }

//    @GetMapping
//    public List<CrossParity> getAllParities() {
//        return crossParityService.findAllActive();
//    }

    @GetMapping("/{id}")
    public ResponseEntity<CrossParity> getParityById(@PathVariable Integer id) {
        return crossParityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/quotes")
    public List<QuoteHistory> getQuoteHistory(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        return quoteHistoryService.findByParityIdAndTimeRange(id, startDateTime, endDateTime);
    }

    @GetMapping("/{id}/dailystats")
    public List<DailyStats> getDailyStats(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return dailyStatsService.findByParityIdAndDateRange(id, startDate, endDate);
    }

    @GetMapping("/{id}/positions")
    public List<Position> getPositions(@PathVariable Integer id) {
        return positionService.findByParityId(id);
    }


//aaaa


    @GetMapping
    public List<CrossParity> getAllCrossParities() {
        return crossParityService.getAllCrossParities();
    }


    @PutMapping("/{id}/favorie")
    public ResponseEntity<CrossParity> updateFavorie(@PathVariable Integer id, @RequestParam boolean favorie) {
        CrossParity updatedCrossParity = crossParityService.updateFavorie(id, favorie);
        return ResponseEntity.ok(updatedCrossParity);
    }

    @GetMapping("/identifiers")
    public List<String> getAllCrossParityIdentifiers() {
        return crossParityService.getAllCrossParityIdentifiers();  // Return a list of identifiers
    }

}
