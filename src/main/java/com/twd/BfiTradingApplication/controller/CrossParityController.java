package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.service.CrossParityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/api/cross-parities")
public class CrossParityController {

    @Autowired
    private CrossParityService crossParityService;

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
