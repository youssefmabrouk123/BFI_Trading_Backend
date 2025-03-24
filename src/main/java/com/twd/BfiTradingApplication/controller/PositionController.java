//    package com.twd.BfiTradingApplication.controller;
//    import com.twd.BfiTradingApplication.dto.PositionDTO;
//    import com.twd.BfiTradingApplication.entity.Position;
//    import com.twd.BfiTradingApplication.service.PositionService;
//    import org.springframework.beans.factory.annotation.Autowired;
//    import org.springframework.http.ResponseEntity;
//    import org.springframework.web.bind.annotation.*;
//
//    import java.math.BigDecimal;
//    import java.util.List;
//
//    @RestController
//    @RequestMapping("public/api/positions")
//    public class PositionController {
//
//        @Autowired
//        private PositionService positionService;
//
//        // Open a new position
//        @PostMapping("/open")
//        public ResponseEntity<Position> openPosition(
//                @RequestParam Integer crossParityId,
//                @RequestParam boolean isLong,
//                @RequestParam Integer quantity,
//                @RequestParam BigDecimal openPrice) {
//            Position position = positionService.openPosition(crossParityId, isLong, quantity, openPrice);
//            return ResponseEntity.ok(position);
//        }
//
//        // Close an existing position
//        @PostMapping("/close/{positionId}")
//        public ResponseEntity<Void> closePosition(@PathVariable Integer positionId) {
//            positionService.closePosition(positionId);
//            return ResponseEntity.ok().build();
//        }
//
//        // Get all open positions with their P/L
//        @GetMapping("/open")
//        public ResponseEntity<List<PositionDTO>> getOpenPositions() {
//            List<PositionDTO> positions = positionService.getOpenPositionsWithProfitLoss();
//            return ResponseEntity.ok(positions);
//        }
//    }


package com.twd.BfiTradingApplication.controller;
import com.twd.BfiTradingApplication.dto.ClosedPositionDTO;
import com.twd.BfiTradingApplication.dto.CrossParitySummaryDTO;
import com.twd.BfiTradingApplication.dto.CurrencyDetailDTO;
import com.twd.BfiTradingApplication.dto.CurrencyTableDTO;
import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("public/api/positions")
public class PositionController {

    @Autowired
    private PositionService positionService;

    // Open or update a position
    @PostMapping("/open")
    public ResponseEntity<Position> openPosition(
            @RequestParam Integer crossParityId,
            @RequestParam boolean isLong,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal openPrice) {
        Position position = positionService.openPosition(crossParityId, isLong, quantity, openPrice);
        return ResponseEntity.ok(position);
    }

    // Close an existing position
    @PostMapping("/close/{positionId}")
    public ResponseEntity<Position> closePosition(
            @PathVariable Integer positionId,
            @RequestParam BigDecimal closePrice) {
        Position position = positionService.closePosition(positionId, closePrice);
        return ResponseEntity.ok(position);
    }

    // Get all open positions with their P/L
    @GetMapping("/open")
    public ResponseEntity<List<PositionDTO>> getOpenPositions() {
        List<PositionDTO> positions = positionService.getOpenPositionsWithProfitLoss();
        return ResponseEntity.ok(positions);
    }

    // Get all closed positions
    @GetMapping("/closed")
    public ResponseEntity<List<ClosedPositionDTO>> getClosedPositions() {
        List<ClosedPositionDTO> positions = positionService.getClosedPositions();
        return ResponseEntity.ok(positions);
    }

    // Cross parity summary (one row per cross)
    @GetMapping("/summary/cross")
    public ResponseEntity<List<CrossParitySummaryDTO>> getCrossParitySummary() {
        List<CrossParitySummaryDTO> summaries = positionService.getCrossParitySummary();
        return ResponseEntity.ok(summaries);
    }

    // Currency details (multiple rows per currency)
    @GetMapping("/summary/currency/details")
    public ResponseEntity<List<CurrencyDetailDTO>> getCurrencyDetails() {
        List<CurrencyDetailDTO> details = positionService.getCurrencyDetails();
        return ResponseEntity.ok(details);
    }

    // Currency table (one row per currency with aggregated data)
    @GetMapping("/summary/currency/table")
    public ResponseEntity<List<CurrencyTableDTO>> getCurrencyTable() {
        List<CurrencyTableDTO> table = positionService.getCurrencyTable();
        return ResponseEntity.ok(table);
    }


    // In PositionController.java
    @GetMapping("/latest-price/{positionId}")
    public ResponseEntity<BigDecimal> getLatestPrice(@PathVariable Integer positionId) {
        BigDecimal latestPrice = positionService.getLatestPrice(positionId);
        return ResponseEntity.ok(latestPrice);
    }
}

