package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.TransactionCurrencyDTO;
import com.twd.BfiTradingApplication.repository.TransactionCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/api/transactions")
public class TransactionCurrencyController {

    @Autowired
    private TransactionCurrencyRepository transactionCurrencyRepository;

    @GetMapping("/history")
    public ResponseEntity<List<TransactionCurrencyDTO>> getTransactionHistory() {
        List<TransactionCurrencyDTO> transactions = transactionCurrencyRepository.findClosedTransactionHistory();
        return ResponseEntity.ok(transactions);
    }
}
