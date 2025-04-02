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

//    @Transactional
//    public Transaction processTransaction(Integer devAchnId, Integer devVenId, BigDecimal mntAcht, BigDecimal price) {
//        Currency devAchn = currencyRepository.findById(devAchnId)
//                .orElseThrow(() -> new RuntimeException("Currency not found with ID: " + devAchnId));
//        Currency devVen = currencyRepository.findById(devVenId)
//                .orElseThrow(() -> new RuntimeException("Currency not found with ID: " + devVenId));
//
//        BigDecimal mntVen = mntAcht.multiply(price);
//        Transaction transaction = new Transaction(devAchn, devVen, mntAcht, mntVen, price);
//        transaction = transactionRepository.save(transaction);
//
//        Position boughtPosition = positionRepository.findByCurrency(devAchn);
//        if (boughtPosition != null) {
//            boughtPosition.setMntDev(boughtPosition.getMntDev().add(mntAcht));
//            if (boughtPosition.getEntryPrice() == null) {
//                boughtPosition.setEntryPrice(price);
//            }
//        } else {
//            boughtPosition = new Position(devAchn, mntAcht, BigDecimal.ZERO);
//        }
//        positionRepository.save(boughtPosition);
//
//        Position soldPosition = positionRepository.findByCurrency(devVen);
//        if (soldPosition != null) {
//            soldPosition.setMntDev(soldPosition.getMntDev().subtract(mntVen));
//            if (soldPosition.getEntryPrice() == null) {
//                soldPosition.setEntryPrice(price);
//            }
//        } else {
//            soldPosition = new Position(devVen, mntVen.negate(), BigDecimal.ZERO    );
//        }
//        positionRepository.save(soldPosition);
//
//        socketIOServer.getBroadcastOperations().sendEvent("positionsUpdate", positionRepository.findAll()); // Émettre après transaction
//        return transaction;
//    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }
}