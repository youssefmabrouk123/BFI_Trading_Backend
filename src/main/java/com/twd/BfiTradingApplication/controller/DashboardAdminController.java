package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.service.DashboardAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public/api/admin")
public class DashboardAdminController {

    private final DashboardAdminService adminService;

    @Autowired
    public DashboardAdminController(DashboardAdminService adminService) {
        this.adminService = adminService;
    }

    // User Endpoints
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = adminService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = adminService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        try {
            User updatedUser = adminService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // CrossParity Endpoints
    @GetMapping("/cross-parities")
    public ResponseEntity<List<CrossParity>> getAllCrossParities() {
        return ResponseEntity.ok(adminService.getAllCrossParities());
    }

    @GetMapping("/cross-parities/{id}")
    public ResponseEntity<CrossParity> getCrossParityById(@PathVariable Integer id) {
        Optional<CrossParity> crossParity = adminService.getCrossParityById(id);
        return crossParity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/cross-parities")
    public ResponseEntity<?> createCrossParity(@RequestBody CrossParity crossParity) {
        try {
            CrossParity createdCrossParity = adminService.createCrossParity(crossParity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCrossParity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/cross-parities/{id}")
    public ResponseEntity<?> updateCrossParity(@PathVariable Integer id, @RequestBody CrossParity crossParityDetails) {
        try {
            CrossParity updatedCrossParity = adminService.updateCrossParity(id, crossParityDetails);
            return ResponseEntity.ok(updatedCrossParity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/cross-parities/{id}")
    public ResponseEntity<?> deleteCrossParity(@PathVariable Integer id) {
        try {
            adminService.deleteCrossParity(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Currency Endpoints
    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        return ResponseEntity.ok(adminService.getAllCurrencies());
    }

    @GetMapping("/currencies/{id}")
    public ResponseEntity<Currency> getCurrencyById(@PathVariable Integer id) {
        Optional<Currency> currency = adminService.getCurrencyById(id);
        return currency.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/currencies")
    public ResponseEntity<?> createCurrency(@RequestBody Currency currency) {
        try {
            Currency createdCurrency = adminService.createCurrency(currency);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCurrency);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/currencies/{id}")
    public ResponseEntity<?> updateCurrency(@PathVariable Integer id, @RequestBody Currency currencyDetails) {
        try {
            Currency updatedCurrency = adminService.updateCurrency(id, currencyDetails);
            return ResponseEntity.ok(updatedCurrency);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/currencies/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Integer id) {
        try {
            adminService.deleteCurrency(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Counterparty Endpoints
    @GetMapping("/counterparties")
    public ResponseEntity<List<Counterparty>> getAllCounterparties() {
        return ResponseEntity.ok(adminService.getAllCounterparties());
    }

    @GetMapping("/counterparties/{id}")
    public ResponseEntity<Counterparty> getCounterpartyById(@PathVariable Integer id) {
        Optional<Counterparty> counterparty = adminService.getCounterpartyById(id);
        return counterparty.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/counterparties")
    public ResponseEntity<?> createCounterparty(@RequestBody Counterparty counterparty) {
        try {
            Counterparty createdCounterparty = adminService.createCounterparty(counterparty);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCounterparty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/counterparties/{id}")
    public ResponseEntity<?> updateCounterparty(@PathVariable Integer id, @RequestBody Counterparty counterpartyDetails) {
        try {
            Counterparty updatedCounterparty = adminService.updateCounterparty(id, counterpartyDetails);
            return ResponseEntity.ok(updatedCounterparty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/counterparties/{id}")
    public ResponseEntity<?> deleteCounterparty(@PathVariable Integer id) {
        try {
            adminService.deleteCounterparty(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Position Endpoints
    @GetMapping("/positions")
    public ResponseEntity<List<Position>> getAllPositions() {
        return ResponseEntity.ok(adminService.getAllPositions());
    }

    @GetMapping("/positions/{id}")
    public ResponseEntity<Position> getPositionById(@PathVariable Integer id) {
        Optional<Position> position = adminService.getPositionById(id);
        return position.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/positions")
    public ResponseEntity<?> createPosition(@RequestBody Position position) {
        try {
            Position createdPosition = adminService.createPosition(position);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/positions/{id}")
    public ResponseEntity<?> updatePosition(@PathVariable Integer id, @RequestBody Position positionDetails) {
        try {
            Position updatedPosition = adminService.updatePosition(id, positionDetails);
            return ResponseEntity.ok(updatedPosition);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/positions/{id}")
    public ResponseEntity<?> deletePosition(@PathVariable Integer id) {
        try {
            adminService.deletePosition(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}