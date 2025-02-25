package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping
    public Currency create(@RequestBody Currency currency) {
        return currencyService.createCurrency(currency);
    }

    @GetMapping("/{id}")
    public Currency getById(@PathVariable Integer id) {
        return currencyService.getCurrencyById(id);
    }

    @GetMapping
    public List<Currency> getAll() {
        return currencyService.getAllCurrencies();
    }

    @PutMapping("/{id}")
    public Currency update(@PathVariable Integer id, @RequestBody Currency currency) {
        return currencyService.updateCurrency(id, currency);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        currencyService.deleteCurrency(id);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Veuillez sélectionner un fichier CSV à télécharger.");
        }

        try {
            currencyService.saveCurrenciesFromCsv(file);
            return ResponseEntity.status(HttpStatus.OK).body("Fichier CSV téléchargé et données enregistrées avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du traitement du fichier CSV.");
        }
    }
}
