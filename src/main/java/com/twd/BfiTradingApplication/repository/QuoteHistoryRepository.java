package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.QuoteHistory;
import com.twd.BfiTradingApplication.entity.QuoteHistoryId;
import com.twd.BfiTradingApplication.entity.CrossParity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuoteHistoryRepository extends JpaRepository<QuoteHistory, QuoteHistoryId> {

    // Utiliser "id.quoteTime" pour référencer la clé composite correctement
    //List<QuoteHistory> findByCrossParityAndIdQuoteTimeBetween(CrossParity crossParity, LocalDateTime startTime, LocalDateTime endTime);
}
