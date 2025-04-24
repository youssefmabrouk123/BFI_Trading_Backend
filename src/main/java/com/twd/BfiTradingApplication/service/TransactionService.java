package com.twd.BfiTradingApplication.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.Transaction;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import com.twd.BfiTradingApplication.repository.PositionRepository;
import com.twd.BfiTradingApplication.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private SocketIOServer socketIOServer;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }
}