package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.service.CrossParityService;
import com.twd.BfiTradingApplication.service.DailyStatsService;
import com.twd.BfiTradingApplication.service.QuoteHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/public/api/cross-parities")
public class CrossParityController {
    private final CrossParityService crossParityService;
    private final QuoteHistoryService quoteHistoryService;
    private final DailyStatsService dailyStatsService;

    @Autowired
    public CrossParityController(CrossParityService crossParityService,
                                 QuoteHistoryService quoteHistoryService,
                                 DailyStatsService dailyStatsService
                                ) {
        this.crossParityService = crossParityService;
        this.quoteHistoryService = quoteHistoryService;
        this.dailyStatsService = dailyStatsService;
    }



    @GetMapping("/{id}")
    public ResponseEntity<CrossParity> getParityById(@PathVariable Integer id) {
        return crossParityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping
    public List<CrossParity> getAllCrossParities() {
        return crossParityService.getAllCrossParities();
    }


//    @PutMapping("/{id}/favorie")
//    public ResponseEntity<CrossParity> updateFavorie(@PathVariable Integer id, @RequestParam boolean favorie) {
//        CrossParity updatedCrossParity = crossParityService.updateFavorie(id, favorie);
//        return ResponseEntity.ok(updatedCrossParity);
//    }

    @GetMapping("/identifiers")
    public List<String> getAllCrossParityIdentifiers() {
        return crossParityService.getAllCrossParityIdentifiers();  // Return a list of identifiers
    }

}
