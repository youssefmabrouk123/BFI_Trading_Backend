package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.dto.TransactionCurrencyDTO;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.TransactionCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionCurrencyRepository extends JpaRepository<TransactionCurrency, Integer> {
    List<TransactionCurrency> findByPosition(Position position);

    @Query("SELECT new com.twd.BfiTradingApplication.dto.TransactionCurrencyDTO(tc.currency.identifier, tc.amount, p.closeTime) " +
            "FROM TransactionCurrency tc " +
            "JOIN tc.position p " +
            "WHERE p.status = 'CLOSED' " +
            "ORDER BY p.closeTime DESC")
    List<TransactionCurrencyDTO> findClosedTransactionHistory();
}

