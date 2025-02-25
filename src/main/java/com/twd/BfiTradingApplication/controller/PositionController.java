package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
public class PositionController {
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public ResponseEntity<Position> openPosition(@RequestBody PositionDTO positionDTO) {
        Position position = positionService.openPosition(positionDTO);
        return ResponseEntity.ok(position);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Position> closePosition(@PathVariable Integer id) {
        Position position = positionService.closePosition(id);
        return ResponseEntity.ok(position);
    }

    @GetMapping
    public ResponseEntity<List<Position>> getAllPositions() {
        return ResponseEntity.ok(positionService.getOpenPositions());
    }

    @GetMapping("/cross-parity/{crossParityId}")
    public ResponseEntity<List<Position>> getPositionsForCrossParity(@PathVariable Integer crossParityId) {
        return ResponseEntity.ok(positionService.getPositionsForCrossParity(crossParityId));
    }
}