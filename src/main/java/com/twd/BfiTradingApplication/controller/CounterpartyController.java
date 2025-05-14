package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.Counterparty;
import com.twd.BfiTradingApplication.service.CounterpartyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counterparties")
public class CounterpartyController {

    private final CounterpartyService service;

    public CounterpartyController(CounterpartyService service) {
        this.service = service;
    }

    // ✅ GET all counterparties
    @GetMapping
    public List<Counterparty> getAllCounterparties() {
        return service.getAllCounterparties();
    }

    // ✅ GET all short names
    @GetMapping("/short-names")
    public List<String> getAllShortNames() {
        return service.getAllShortNames();
    }

    // ✅ GET one by id
    @GetMapping("/{id}")
    public ResponseEntity<Counterparty> getCounterparty(@PathVariable Integer id) {
        return service.getCounterpartyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ POST create new counterparty
    @PostMapping
    public ResponseEntity<Counterparty> createCounterparty(@RequestBody Counterparty counterparty) {
        Counterparty saved = service.addCounterparty(counterparty);
        return ResponseEntity.ok(saved);
    }

    // ✅ DELETE counterparty
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCounterparty(@PathVariable Integer id) {
        service.deleteCounterparty(id);
        return ResponseEntity.noContent().build();
    }
}