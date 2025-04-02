//package com.twd.BfiTradingApplication.controller;
//
//import com.twd.BfiTradingApplication.dto.PositionDTO;
//import com.twd.BfiTradingApplication.service.PositionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/public/api/positions")
//public class PositionController {
//
//    @Autowired
//    private PositionService positionService;
//
//    @GetMapping
//    public ResponseEntity<List<PositionDTO>> getAllPositions() {
//        List<PositionDTO> positions = positionService.getAllPositions();
//        return ResponseEntity.ok(positions);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<PositionDTO> getPositionById(@PathVariable Integer id) {
//        return positionService.getPositionById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<PositionDTO> createPosition(@RequestBody PositionDTO positionDTO) {
//        PositionDTO createdPosition = positionService.createPosition(positionDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
//    }
//
//    // Nouvel endpoint pour créer une liste de positions
//    @PostMapping("/bulk")
//    public ResponseEntity<List<PositionDTO>> createPositions(@RequestBody List<PositionDTO> positionDTOs) {
//        List<PositionDTO> createdPositions = positionService.createPositions(positionDTOs);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdPositions);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<PositionDTO> updatePosition(@PathVariable Integer id,
//                                                      @RequestBody PositionDTO positionDTO) {
//        return positionService.updatePosition(id, positionDTO)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePosition(@PathVariable Integer id) {
//        if (positionService.deletePosition(id)) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//}